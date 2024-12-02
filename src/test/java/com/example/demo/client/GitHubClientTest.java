package com.example.demo.client;

import com.example.demo.exception.RepositoryNotFoundException;
import com.example.demo.model.dto.GitHubRepository;
import com.example.demo.model.dto.Owner;
import com.example.demo.model.dto.RepositoryDto;
import com.example.demo.model.entity.Repository;
import com.example.demo.repository.RepositoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWireMock(port = 8089)
@NoArgsConstructor
public class GitHubClientTest {

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RepositoryRepository repositoryRepository;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        wireMockServer.start();
        restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8089")
                .build();
        repositoryRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    private static final String USERNAME = "budzikovy";

    private static final String REPOSITORY_NAME = "jwt-authentication";

    private static final String GITHUB_URL = "/repos/" + USERNAME + "/" + REPOSITORY_NAME;

    GitHubRepository gitHubRepository = GitHubRepository.builder()
            .id(1)
            .fullName("budzikovy/jwt-authentication")
            .description("feign test")
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

    String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8088")
            .path("/repositories/" + USERNAME + "/" + REPOSITORY_NAME)
            .toUriString();

    @Test
    public void getDetails_DataCorrect_RepositoryReturned() throws JsonProcessingException {
        wireMockServer.stubFor(get(urlEqualTo(GITHUB_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(gitHubRepository))));

        GitHubRepository result = restTemplate.getForObject(url, GitHubRepository.class);

        assertThat(result).isNotNull();
        assertEquals(REPOSITORY_NAME, result.getRepositoryName());
        assertEquals(USERNAME, result.getOwner().getLogin());
        assertEquals(gitHubRepository.getDescription(), result.getDescription());
        assertEquals(gitHubRepository.getCloneUrl(), result.getCloneUrl());
        assertEquals(gitHubRepository.getFullName(), result.getFullName());
        assertEquals(gitHubRepository.getStars(), result.getStars());
        assertEquals(gitHubRepository.getCreatedAt(), result.getCreatedAt());

    }

    @Test
    public void saveRepository_DataCorrect_RepositoryDtoReturned() throws JsonProcessingException {
        wireMockServer.stubFor(get(urlEqualTo(GITHUB_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(repositoryDto))));

        ResponseEntity<RepositoryDto> result = restTemplate.postForEntity(url, repository, RepositoryDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        RepositoryDto savedRepository = result.getBody();

        assertThat(savedRepository).isNotNull();
        assertEquals(repository.getDescription(), savedRepository.getDescription());
        assertEquals(repository.getCloneUrl(), savedRepository.getCloneUrl());
        assertEquals(repository.getFullName(), savedRepository.getFullName());
        assertEquals(repository.getStars(), savedRepository.getStars());
        assertEquals(repository.getCreatedAt(), savedRepository.getCreatedAt());

        Repository savedRepositoryLocally = repositoryRepository.findByOwnerAndRepositoryName(USERNAME, REPOSITORY_NAME)
                .orElseThrow(() -> new RepositoryNotFoundException(REPOSITORY_NAME, USERNAME));

        assertEquals(repository.getDescription(), savedRepositoryLocally.getDescription());
        assertEquals(repository.getCloneUrl(), savedRepositoryLocally.getCloneUrl());
        assertEquals(repository.getFullName(), savedRepositoryLocally.getFullName());
        assertEquals(repository.getStars(), savedRepositoryLocally.getStars());
        assertEquals(repository.getCreatedAt(), savedRepositoryLocally.getCreatedAt());

    }

    @Test
    public void getRepositoryDetails_DataCorrect_RepositoryDtoReturned() {

        String urlLocal = UriComponentsBuilder.fromHttpUrl("http://localhost:8088")
                .path("/local/repositories/" + USERNAME + "/" + REPOSITORY_NAME)
                .toUriString();

        repositoryRepository.save(repository);

        ResponseEntity<RepositoryDto> result = restTemplate.exchange(urlLocal, HttpMethod.GET, HttpEntity.EMPTY, RepositoryDto.class);

        assertNotNull(result.getBody());

        assertEquals(HttpStatus.OK, result.getStatusCode());

        assertEquals(repository.getFullName(), result.getBody().getFullName());
        assertEquals(repository.getDescription(), result.getBody().getDescription());
        assertEquals(repository.getCloneUrl(), result.getBody().getCloneUrl());
        assertEquals(repository.getStars(), result.getBody().getStars());
        assertEquals(repository.getCreatedAt(), result.getBody().getCreatedAt());

    }

    @Test
    public void editRepository_DataCorrect_RepositoryDtoReturned() {

        repositoryRepository.save(repository);

        RepositoryDto editedRepository = RepositoryDto.builder()
                .fullName("budzikofy/jwt-authentication")
                .description("change")
                .cloneUrl("https://github.com/budzikofy/jwt-authentication.git")
                .build();

        RepositoryDto result = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(editedRepository), RepositoryDto.class).getBody();

        assertNotNull(result);
        assertEquals(editedRepository.getFullName(), result.getFullName());
        assertEquals(editedRepository.getDescription(), result.getDescription());
        assertEquals(editedRepository.getStars(), result.getStars());
        assertEquals(editedRepository.getCloneUrl(), result.getCloneUrl());

        Repository updatedRepository = repositoryRepository.findByOwnerAndRepositoryName(USERNAME, REPOSITORY_NAME)
                .orElseThrow(() -> new AssertionError("Repository not found in database"));

        assertEquals(editedRepository.getFullName(), updatedRepository.getFullName());
        assertEquals(editedRepository.getDescription(), updatedRepository.getDescription());
        assertEquals(editedRepository.getStars(), updatedRepository.getStars());
        assertEquals(editedRepository.getCloneUrl(), updatedRepository.getCloneUrl());

    }

    @Test
    public void deleteRepositoryByOwnerAndName_DataCorrect_RepositoryDeleted() {

        repositoryRepository.save(repository);

        Repository savedRepository = repositoryRepository.findByOwnerAndRepositoryName(repository.getOwner(), repository.getRepositoryName())
                .orElseThrow(() -> new AssertionError("Repository not saved in database"));
        assertNotNull(savedRepository);

        ResponseEntity<Void> result = restTemplate.exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        Optional<Repository> deletedRepository = repositoryRepository.findByOwnerAndRepositoryName(repository.getOwner(), repository.getRepositoryName());
        assertTrue(deletedRepository.isEmpty(), "Repository was not deleted from database");

    }
}