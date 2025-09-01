package com.vallegrande.webfluxai.controller;

import com.vallegrande.webfluxai.dto.JobSearchRequest;
import com.vallegrande.webfluxai.dto.JobSearchResponse;
import com.vallegrande.webfluxai.model.JobSearchResult;
import com.vallegrande.webfluxai.service.JSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JSearchController {

    private final JSearchService jSearchService;

    @GetMapping("/search")
    public Mono<ResponseEntity<JobSearchResult>> searchJobs(
            @RequestParam String query,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer resultsPerPage) {
        
        var request = new JobSearchRequest();
        request.setQuery(query);
        request.setLocation(location);
        request.setPage(page);
        request.setResultsPerPage(resultsPerPage);

        return jSearchService.searchJobs(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/details/{jobId}")
    public Mono<ResponseEntity<JobSearchResponse.Job>> getJobDetails(@PathVariable String jobId) {
        return jSearchService.getJobDetails(jobId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<JobSearchResult>> getSearchResultById(@PathVariable String id) {
        return jSearchService.getSearchResultById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
