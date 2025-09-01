package com.vallegrande.webfluxai.service;

import com.vallegrande.webfluxai.config.ApiProperties;
import com.vallegrande.webfluxai.dto.JobSearchRequest;
import com.vallegrande.webfluxai.dto.JobSearchResponse;
import com.vallegrande.webfluxai.exception.ApiException;
import com.vallegrande.webfluxai.model.JobSearchResult;
import com.vallegrande.webfluxai.repository.JobSearchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final WebClient webClient;
    private final JobSearchResultRepository repository;
    private final ApiProperties apiProperties;

    public Mono<JobSearchResult> searchJobs(JobSearchRequest request) {
        log.info("Searching jobs with query: {}, location: {}", request.getQuery(), request.getLocation());
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("query", request.getQuery())
                        .queryParam("location", request.getLocation())
                        .queryParam("page", request.getPage())
                        .queryParam("num_pages", request.getResultsPerPage())
                        .build())
                .header("X-RapidAPI-Key", apiProperties.getJsearch().getApiKey())
                .header("X-RapidAPI-Host", apiProperties.getJsearch().getApiHost())
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
                .bodyToMono(JobSearchResponse.class)
                .flatMap(response -> {
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
                    result.setResultsPerPage(request.getResultsPerPage());
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
                    if (response.getMeta() != null) {
                        metadata.put("totalResults", response.getMeta().getTotal());
                        metadata.put("currentPage", response.getMeta().getPage());
                        metadata.put("resultsPerPage", response.getMeta().getPerPage());
                    }
                    result.setMetadata(metadata);

                    return repository.save(result);
                });
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
