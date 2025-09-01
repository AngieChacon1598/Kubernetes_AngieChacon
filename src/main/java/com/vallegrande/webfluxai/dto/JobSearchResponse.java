package com.vallegrande.webfluxai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class JobSearchResponse {
    private List<Job> data;
    private Meta meta;

    @Data
    public static class Job {
        @JsonProperty("job_id")
        private String jobId;
        private String title;
        @JsonProperty("employer_name")
        private String employerName;
        private String location;
        @JsonProperty("job_type")
        private String jobType;
        @JsonProperty("job_description")
        private String jobDescription;
        @JsonProperty("job_apply_link")
        private String jobApplyLink;
        @JsonProperty("job_posted_at_datetime_utc")
        private String jobPostedAt;
        @JsonProperty("job_required_skills")
        private List<String> jobRequiredSkills;
    }

    @Data
    public static class Meta {
        @JsonProperty("total")
        private int total;
        @JsonProperty("page")
        private int page;
        @JsonProperty("per_page")
        private int perPage;
    }
}
