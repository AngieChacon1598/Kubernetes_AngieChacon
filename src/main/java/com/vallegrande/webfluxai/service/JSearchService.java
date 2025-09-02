package com.vallegrande.webfluxai.service;

import com.vallegrande.webfluxai.config.ApiProperties;
import com.vallegrande.webfluxai.dto.JobSearchRequest;
import com.vallegrande.webfluxai.dto.JobSearchResponse;
import com.vallegrande.webfluxai.exception.ApiException;
import com.vallegrande.webfluxai.model.JobSearchResult;
import com.vallegrande.webfluxai.repository.JobSearchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JSearchService {

    @Qualifier("jsearchWebClient")
    private final WebClient jsearchWebClient;
    private final JobSearchResultRepository repository;
    private final ApiProperties apiProperties;

    public Mono<JobSearchResult> searchJobs(JobSearchRequest request) {
        log.info("Searching jobs with query: {}, location: {}", request.getQuery(), request.getLocation());
        
        // Determinar país basado en la ubicación
        final String country = determineCountry(request.getLocation());
        log.info("Using country code: {} for location: {}", country, request.getLocation());
        
        // Limitar el número de resultados para evitar respuestas muy grandes
        int numPages = Math.min(request.getResultsPerPage(), 5); // Máximo 5 páginas por defecto
        
        return jsearchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("query", request.getQuery())
                        .queryParam("page", request.getPage())
                        .queryParam("num_pages", numPages) // Usar el valor limitado
                        .queryParam("country", country) // País detectado automáticamente
                        .queryParam("date_posted", "all") // All dates
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("Error searching jobs: {}", error);
                                    return Mono.error(new ApiException(
                                            HttpStatus.valueOf(clientResponse.statusCode().value()),
                                            "Error searching jobs: " + error
                                    ));
                                })
                )
                .bodyToMono(String.class) // Primero obtenemos como String
                .doOnNext(responseBody -> log.info("Raw response received, length: {} bytes", responseBody.length()))
                .flatMap(responseBody -> {
                    try {
                        // Parsear JSON manualmente
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        JobSearchResponse response = mapper.readValue(responseBody, JobSearchResponse.class);
                        
                        log.info("JSON parsed successfully. Status: {}, Data size: {}", 
                                response.getStatus(), response.getData() != null ? response.getData().size() : 0);
                        
                        if (response.getData() == null || response.getData().isEmpty()) {
                            return Mono.error(new ApiException(
                                    HttpStatus.NOT_FOUND,
                                    "No jobs found matching the criteria"
                            ));
                        }

                        var result = new JobSearchResult();
                        result.setQuery(request.getQuery());
                        result.setLocation(request.getLocation());
                        result.setPage(request.getPage());
                        result.setResultsPerPage(numPages); // Usar el valor limitado
                        result.setSearchedAt(LocalDateTime.now());

                        // Convertir los trabajos al formato de nuestro modelo
                        var jobs = response.getData().stream()
                                .map(job -> {
                                    var jobResult = new JobSearchResult.Job();
                                    jobResult.setJobId(job.getJobId());
                                    jobResult.setTitle(job.getTitle());
                                    jobResult.setCompanyName(job.getEmployerName());
                                    jobResult.setLocation(job.getLocation());
                                    jobResult.setJobType(job.getJobType());
                                    jobResult.setDescription(job.getJobDescription());
                                    jobResult.setApplyLink(job.getJobApplyLink());
                                    jobResult.setPostedAt(parseDateTime(job.getJobPostedAt()));
                                    jobResult.setRequiredSkills(job.getJobRequiredSkills());
                                    return jobResult;
                                })
                                .collect(Collectors.toList());

                        result.setJobs(jobs);

                        // Agregar metadatos de la respuesta
                        Map<String, Object> metadata = new HashMap<>();
                        metadata.put("status", response.getStatus());
                        metadata.put("requestId", response.getRequestId());
                        if (response.getParameters() != null) {
                            metadata.put("searchQuery", response.getParameters().getQuery());
                            metadata.put("searchPage", response.getParameters().getPage());
                            metadata.put("searchCountry", response.getParameters().getCountry());
                        }
                        metadata.put("totalResults", response.getData().size());
                        metadata.put("responseSizeBytes", responseBody.length());
                        result.setMetadata(metadata);

                        return repository.save(result);
                    } catch (Exception e) {
                        log.error("Error processing response: {}", e.getMessage(), e);
                        return Mono.error(new ApiException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Error processing response: " + e.getMessage()
                        ));
                    }
                })
                .onErrorMap(throwable -> {
                    log.error("Unexpected error in searchJobs: {}", throwable.getMessage(), throwable);
                    if (throwable instanceof ApiException) {
                        return throwable;
                    }
                    return new ApiException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "An unexpected error occurred: " + throwable.getMessage()
                    );
                });
    }

    // Método adicional para obtener detalles de un trabajo específico
    public Mono<JobSearchResponse.Job> getJobDetails(String jobId) {
        log.info("Getting job details for jobId: {}", jobId);
        
        return jsearchWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/job-details")
                        .queryParam("job_id", jobId)
                        .queryParam("country", "us")
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("Error getting job details: {}", error);
                                    return Mono.error(new ApiException(
                                            HttpStatus.valueOf(clientResponse.statusCode().value()),
                                            "Error getting job details: " + error
                                    ));
                                })
                )
                .bodyToMono(JobSearchResponse.class)
                .flatMap(response -> {
                    if (response.getData() == null || response.getData().isEmpty()) {
                        return Mono.error(new ApiException(
                                HttpStatus.NOT_FOUND,
                                "Job details not found for jobId: " + jobId
                        ));
                    }
                    return Mono.just(response.getData().get(0));
                });
    }

    private String determineCountry(String location) {
        if (location == null) return "us";
        
        String loc = location.toLowerCase();
        if (loc.contains("peru") || loc.contains("trujillo") || loc.contains("lima")) {
            return "pe";
        } else if (loc.contains("mexico") || loc.contains("cdmx")) {
            return "mx";
        } else if (loc.contains("spain") || location.contains("madrid") || location.contains("barcelona")) {
            return "es";
        }
        return "us"; // Default
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr) : null;
        } catch (Exception e) {
            log.warn("Error parsing date time: {}", dateTimeStr, e);
            return null;
        }
    }

    public Mono<JobSearchResult> getSearchResultById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ApiException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Job search result not found with id: " + id
                )));
    }

    public Flux<JobSearchResult> getAllSearchResults() {
        return repository.findAll();
    }

    public Mono<Void> deleteSearchResult(String id) {
        return repository.deleteById(id);
    }
}
