package com.example.demo.controller;

import com.example.demo.client.model.dto.GitHubRepository;
import com.example.demo.client.model.dto.Owner;
import com.example.demo.client.model.dto.RepositoryDto;
import com.example.demo.client.model.entity.Repository;
import com.example.demo.client.service.ResponseService;
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

    GitHubRepository gitHubRepository = GitHubRepository.builder()
            .id(1)
            .fullName("budzikovy/jwt-authentication")
            .description("test endpoint put")
            .cloneUrl("https://github.com/budzikovy/jwt-authentication.git")
            .stars(1)
            .createdAt("2024-10-25T11:58:20Z")
            .owner(new Owner("budzikovy"))
            .repositoryName("jwt-authentication")
            .build();

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
    void getDetails_ShouldReturnRepository() throws Exception {
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

//    @Test
//    void saveRepository_ShouldReturnRepository() throws Exception {
//        Mockito.when(responseService.saveRepository(repository.getOwner(), repository.getRepositoryName(), any(Repository.class))).thenReturn(repositoryDto);
//
//        mockMvc.perform(post("/repositories/budzikovy/jwt-authentication")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(repository)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.owner").value("budzikovy"))
//                .andExpect(jsonPath("$.repositoryName").value("jwt-authentication"));
//
//    }



}
