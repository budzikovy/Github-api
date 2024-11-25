package com.example.demo.client.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RepositoryDto {

    private Long id;
    private String fullName;
    private String description;
    private String cloneUrl;
    private int stars;
    private String createdAt;

}
