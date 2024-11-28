package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GitHubRepository {

    private int id;
    @JsonProperty("full_name")
    private String fullName;

    private String description;

    @JsonProperty("clone_url")
    private String cloneUrl;

    @JsonProperty("stargazers_count")
    private int stars;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("name")
    private String repositoryName;
}
