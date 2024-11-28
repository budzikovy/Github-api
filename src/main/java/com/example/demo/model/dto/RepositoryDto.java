package com.example.demo.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryDto {

    private Long id;
    private String fullName;
    private String description;
    private String cloneUrl;
    private Integer stars;
    private String createdAt;

}
