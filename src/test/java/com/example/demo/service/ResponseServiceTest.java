package com.example.demo.service;

import com.example.demo.client.GitHubClient;
import com.example.demo.exception.RepositoryExistsException;
import com.example.demo.exception.RepositoryNotFoundException;
import com.example.demo.exception.RepositoryNotFoundGitHubException;
import com.example.demo.mapper.RepositoryMapper;
import com.example.demo.model.dto.GitHubRepository;
import com.example.demo.model.dto.Owner;
import com.example.demo.model.dto.RepositoryDto;
import com.example.demo.model.entity.Repository;
import com.example.demo.repository.RepositoryRepository;
import com.example.demo.validation.RepositoryValidation;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ResponseServiceTest {

    RepositoryService responseService;
    GitHubClient gitHubClient;
    RepositoryMapper repositoryMapper;
    RepositoryRepository repositoryRepository;
    RepositoryValidation repositoryValidation;

    @BeforeEach
    void setup() {
        this.repositoryRepository = Mockito.mock(RepositoryRepository.class);
        this.repositoryMapper = Mappers.getMapper(RepositoryMapper.class);
        this.gitHubClient = Mockito.mock(GitHubClient.class);
        this.repositoryValidation = Mockito.mock(RepositoryValidation.class);
        this.responseService = new RepositoryService(gitHubClient, repositoryMapper, repositoryRepository, repositoryValidation);
    }

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

    String exMessage = "Repository named " + repository.getRepositoryName() + " by " + repository.getOwner() + " not found in database.";

    @Test
    void saveRepository_DataCorrect_RepositoryDtoReturned() {
        when(repositoryValidation.isRepositoryNull(null)).thenReturn(true);
        when(gitHubClient.getDetails("budzikovy", "jwt-authentication")).thenReturn(gitHubRepository);
        when(repositoryRepository.save(Mockito.any())).thenReturn(repository);

        RepositoryDto result = responseService.saveRepository("budzikovy", "jwt-authentication", null);

        assertNotNull(result);
        assertEquals("budzikovy/jwt-authentication", result.getFullName());
        assertEquals("test endpoint put", result.getDescription());
        assertEquals("https://github.com/budzikovy/jwt-authentication.git", result.getCloneUrl());
        assertEquals(1, result.getStars());
        assertEquals("2024-10-25T11:58:20Z", result.getCreatedAt());
    }

    @Test
    void saveRepository_RepositoryExists_RepositoryExistsExceptionThrown() {
        Mockito.doThrow(new RepositoryExistsException(repository.getRepositoryName(), repository.getOwner()))
                .when(repositoryValidation).validateRepositoryExists(repository.getOwner(), repository.getRepositoryName());

        RepositoryExistsException exception = assertThrows(RepositoryExistsException.class, () ->
                responseService.saveRepository(repository.getOwner(), repository.getRepositoryName(), repository));

        assertEquals("Repository named " + repository.getRepositoryName() + " by " + repository.getOwner() + " already exists in database.", exception.getMessage());
    }

    @Test
    void getDetails_DataCorrect_RepositoryReturned() {

        when(gitHubClient.getDetails("budzikovy", "jwt-authentication")).thenReturn(gitHubRepository);

        Repository result = responseService.getDetails(repository.getOwner(), repository.getRepositoryName());

        assertNotNull(result);
        assertEquals("budzikovy/jwt-authentication", result.getFullName());
        assertEquals("test endpoint put", result.getDescription());
        assertEquals("https://github.com/budzikovy/jwt-authentication.git", result.getCloneUrl());
        assertEquals(1, result.getStars());
        assertEquals("2024-10-25T11:58:20Z", result.getCreatedAt());
    }

    @Test
    void getDetails_RepositoryNotFound_RepositoryNotFoundExceptionThrown() {

        when(gitHubClient.getDetails(repository.getOwner(), "123")).thenThrow(FeignException.NotFound.class);

        RepositoryNotFoundGitHubException exception = assertThrows(RepositoryNotFoundGitHubException.class, () ->
                responseService.getDetails(repository.getOwner(), "123"));

        assertEquals("GitHub api cannot find repository named 123 by budzikovy.", exception.getMessage());

    }

    @Test
    void getDetails_RepositoryNotFound_RepositoryNotFoundGitHubExceptionThrown() {
        when(gitHubClient.getDetails("budzikovy", "jwt-authentication"))
                .thenThrow(new FeignException.NotFound("Not Found", Mockito.mock(Request.class), null, null));

        assertThrows(RepositoryNotFoundGitHubException.class, () ->
                responseService.getDetails("budzikovy", "jwt-authentication"));
    }

    @Test
    void getRepositoryDetails_DataCorrect_RepositoryDtoReturned() {

        when(repositoryRepository.findByOwnerAndRepositoryName(repository.getOwner(), repository.getRepositoryName()))
                .thenReturn(Optional.of(repository));

        RepositoryDto result = responseService.getRepositoryDetails("budzikovy", "jwt-authentication");

        assertNotNull(result);
        assertEquals("budzikovy/jwt-authentication", result.getFullName());
        assertEquals("test endpoint put", result.getDescription());
        assertEquals("https://github.com/budzikovy/jwt-authentication.git", result.getCloneUrl());
        assertEquals(1, result.getStars());
        assertEquals("2024-01-01T00:00:00Z", result.getCreatedAt());

    }

    @Test
    void getRepositoryDetails_RepositoryNotFound_RepositoryNotFoundExceptionThrown() {

        when(repositoryRepository.findByOwnerAndRepositoryName(repository.getOwner(), repository.getRepositoryName()))
                .thenReturn(Optional.empty());

        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class, () ->
                responseService.getRepositoryDetails(repository.getOwner(), repository.getRepositoryName()));

        assertEquals(exMessage, exception.getMessage());

    }

    @Test
    void deleteRepository_DataCorrect_RepositoryDtoReturned() {

        when(repositoryRepository.findByOwnerAndRepositoryName(repository.getOwner(), repository.getRepositoryName()))
                .thenReturn(Optional.of(repository));

        RepositoryDto result = responseService.deleteRepository("budzikovy", "jwt-authentication");

        assertNotNull(result);
        assertEquals("budzikovy/jwt-authentication", result.getFullName());
        assertEquals("test endpoint put", result.getDescription());
        assertEquals("https://github.com/budzikovy/jwt-authentication.git", result.getCloneUrl());
        assertEquals(1, result.getStars());
        assertEquals("2024-01-01T00:00:00Z", result.getCreatedAt());

    }

    @Test
    void deleteRepository_RepositoryNotFound_RepositoryNotFoundExceptionThrown() {

        when(repositoryRepository.findByOwnerAndRepositoryName(repository.getOwner(), repository.getRepositoryName()))
                .thenReturn(Optional.empty());

        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class, () ->
                responseService.deleteRepository(repository.getOwner(), repository.getRepositoryName()));

        assertEquals(exMessage, exception.getMessage());

    }

    @Test
    void editRepository_DataCorrect_RepositoryDtoReturned() {

        Repository updatedRepository = Repository.builder()
                .id(1L)
                .owner("budzikofy")
                .repositoryName("test")
                .fullName("budzikofy/test")
                .description("test")
                .cloneUrl("https://github.com/test")
                .stars(2)
                .createdAt("2024-01-01T00:00:00Z")
                .build();

        when(repositoryRepository.findByOwnerAndRepositoryName("budzikovy", "jwt-authentication"))
                .thenReturn(Optional.of(repository));
        when(repositoryRepository.save(repository)).thenReturn(updatedRepository);

        RepositoryDto result = responseService.editRepository(repository.getOwner(), repository.getRepositoryName(), updatedRepository);

        assertEquals("budzikofy/test", result.getFullName());
        assertEquals("test", result.getDescription());
        assertEquals("https://github.com/test", result.getCloneUrl());
        assertEquals(2, result.getStars());
        assertEquals("2024-01-01T00:00:00Z", result.getCreatedAt());

    }

    @Test
    void editRepository_RepositoryNotFound_RepositoryNotFoundExceptionThrown() {

        Repository editedRepository = Repository.builder()
                .id(1L)
                .owner("budzikovy")
                .repositoryName("jwt-authentication")
                .fullName("budzikovy/jwt-authentication")
                .description("test endpoint put")
                .cloneUrl("https://github.com/budzikovy/jwt-authentication.git")
                .stars(1)
                .createdAt("2024-01-01T00:00:00Z")
                .build();

        when(repositoryRepository.findByOwnerAndRepositoryName(repository.getOwner(), repository.getRepositoryName()))
                .thenReturn(Optional.empty());

        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class, () ->
                responseService.editRepository(repository.getOwner(), repository.getRepositoryName(), editedRepository));

        assertEquals(exMessage, exception.getMessage());

    }

}
