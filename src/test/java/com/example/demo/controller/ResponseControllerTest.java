package com.example.demo.controller;

import com.example.demo.model.dto.RepositoryDto;
import com.example.demo.model.entity.Repository;
import com.example.demo.service.ResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ResponseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResponseService responseService;

    Repository repository = Repository.builder()
            .id(1L)
            .owner("budzikovy")
            .repositoryName("jwt-authentication")
            .fullName("budzikovy/jwt-authentication")
            .description("test endpoint put")
            .cloneUrl("https://github.com/budzikovy/jwt-authentication.git")
            .stars(1)
            .createdAt("2024-01-01T00:00:00Z")
            .build();

    RepositoryDto repositoryDto = RepositoryDto.builder()
            .id(1L)
            .fullName("budzikovy/jwt-authentication")
            .description("test endpoint put")
            .cloneUrl("https://github.com/budzikovy/jwt-authentication.git")
            .stars(1)
            .createdAt("2024-01-01T00:00:00Z")
            .build();

    @Test
    void getDetails_DataCorrect_ReturnStatus200() throws Exception {
        Mockito.when(responseService.getDetails(repository.getOwner(), repository.getRepositoryName())).thenReturn(repository);

        mockMvc.perform(get("/repositories/budzikovy/jwt-authentication"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.owner").value("budzikovy"))
                .andExpect(jsonPath("$.repositoryName").value("jwt-authentication"))
                .andExpect(jsonPath("$.fullName").value("budzikovy/jwt-authentication"))
                .andExpect(jsonPath("$.description").value("test endpoint put"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/budzikovy/jwt-authentication.git"))
                .andExpect(jsonPath("$.stars").value(1))
                .andExpect(jsonPath("$.createdAt").value("2024-01-01T00:00:00Z"));
    }

    @Test
    void saveRepository_DataCorrect_ReturnStatus200() throws Exception {
        Mockito.when(responseService.saveRepository(repository.getOwner(), repository.getRepositoryName(), repository))
                .thenReturn(repositoryDto);

        mockMvc.perform(post("/repositories/budzikovy/jwt-authentication")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(repository)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(repositoryDto.getId()))
                .andExpect(jsonPath("$.fullName").value(repositoryDto.getFullName()))
                .andExpect(jsonPath("$.description").value(repositoryDto.getDescription()))
                .andExpect(jsonPath("$.cloneUrl").value(repositoryDto.getCloneUrl()))
                .andExpect(jsonPath("$.stars").value(repositoryDto.getStars()))
                .andExpect(jsonPath("$.createdAt").value(repositoryDto.getCreatedAt()));
    }

    @Test
    void getRepositoryDetails_DataCorrect_ReturnStatus200() throws Exception {
        Mockito.when(responseService.getRepositoryDetails(repository.getOwner(), repository.getRepositoryName()))
                .thenReturn(repositoryDto);

        mockMvc.perform(get("/repositories/local/budzikovy/jwt-authentication"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(repositoryDto.getId()))
                .andExpect(jsonPath("$.fullName").value(repositoryDto.getFullName()))
                .andExpect(jsonPath("$.description").value(repositoryDto.getDescription()))
                .andExpect(jsonPath("$.cloneUrl").value(repositoryDto.getCloneUrl()))
                .andExpect(jsonPath("$.stars").value(repositoryDto.getStars()))
                .andExpect(jsonPath("$.createdAt").value(repositoryDto.getCreatedAt()));
    }

    @Test
    void editRepository_DataCorrect_ReturnStatus200() throws Exception {
        Mockito.when(responseService.editRepository(repository.getOwner(), repository.getRepositoryName(), repository))
                .thenReturn(repositoryDto);

        mockMvc.perform(put("/repositories/local/budzikovy/jwt-authentication")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(repository)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(repositoryDto.getId()))
                .andExpect(jsonPath("$.fullName").value(repositoryDto.getFullName()))
                .andExpect(jsonPath("$.description").value(repositoryDto.getDescription()))
                .andExpect(jsonPath("$.cloneUrl").value(repositoryDto.getCloneUrl()))
                .andExpect(jsonPath("$.stars").value(repositoryDto.getStars()))
                .andExpect(jsonPath("$.createdAt").value(repositoryDto.getCreatedAt()));
    }

    @Test
    void deleteRepositoryByOwnerAndName_DataCorrect_ReturnStatus200() throws Exception {
        Mockito.when(responseService.deleteRepository(repository.getOwner(), repository.getRepositoryName()))
                .thenReturn(repositoryDto);

        mockMvc.perform(delete("/repositories/local/budzikovy/jwt-authentication"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(repositoryDto.getId()))
                .andExpect(jsonPath("$.fullName").value(repositoryDto.getFullName()))
                .andExpect(jsonPath("$.description").value(repositoryDto.getDescription()))
                .andExpect(jsonPath("$.cloneUrl").value(repositoryDto.getCloneUrl()))
                .andExpect(jsonPath("$.stars").value(repositoryDto.getStars()))
                .andExpect(jsonPath("$.createdAt").value(repositoryDto.getCreatedAt()));
    }
}