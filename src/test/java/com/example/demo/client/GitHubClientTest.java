package com.example.demo.client;

import com.example.demo.model.dto.GitHubRepository;
import com.example.demo.model.dto.Owner;
import com.example.demo.model.dto.RepositoryDto;
import com.example.demo.model.entity.Repository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWireMock(port = 8089)
@NoArgsConstructor
public class GitHubClientTest {

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    GitHubClient gitHubClient;

    @Autowired
    ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        wireMockServer.start();
        restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8089")
                .build();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    private static final String USERNAME = "budzikovy";

    private static final String REPOSITORY_NAME = "jwt-authentication";

    private static final String GITHUB_URL = "/repos/" + USERNAME + "/" + REPOSITORY_NAME;
    private static final String BASE_URL = "/repositories/" + USERNAME + "/" + REPOSITORY_NAME;

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

    String url = String.format("/repositories/%s/%s", USERNAME, REPOSITORY_NAME);

    @Test
    public void getDetails_DataCorrect_RepositoryReturned() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(gitHubRepository);

        wireMockServer.stubFor(get(urlEqualTo(GITHUB_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        String urlGitHub = String.format("/repos/%s/%s", USERNAME, REPOSITORY_NAME);
        GitHubRepository result = restTemplate.getForObject(urlGitHub, GitHubRepository.class);

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
        String request = objectMapper.writeValueAsString(repositoryDto);
        String response = objectMapper.writeValueAsString(repository);

        wireMockServer.stubFor(post(urlEqualTo(BASE_URL))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(request))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        Repository savedRepository = restTemplate.postForObject(url, repositoryDto, Repository.class);

        assertThat(savedRepository).isNotNull();
        assertEquals(REPOSITORY_NAME, savedRepository.getRepositoryName());
        assertEquals(USERNAME, savedRepository.getOwner());
        assertEquals(repository.getDescription(), savedRepository.getDescription());
        assertEquals(repository.getCloneUrl(), savedRepository.getCloneUrl());
        assertEquals(repository.getFullName(), savedRepository.getFullName());
        assertEquals(repository.getStars(), savedRepository.getStars());
        assertEquals(repository.getCreatedAt(), savedRepository.getCreatedAt());
    }

    @Test
    public void editRepository_DataCorrect_RepositoryDtoReturned() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(repositoryDto);
        String response = objectMapper.writeValueAsString(repository);

        wireMockServer.stubFor(put(urlEqualTo(BASE_URL))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(request))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        restTemplate.put(url, repositoryDto, Repository.class);

        wireMockServer.verify(putRequestedFor(urlEqualTo("/repositories/" + USERNAME + "/" + REPOSITORY_NAME))
                .withRequestBody(equalToJson(request)));
    }

    @Test
    public void deleteRepositoryByOwnerAndName_DataCorrect_RepositoryDtoReturned() {
        wireMockServer.stubFor(delete(urlEqualTo(BASE_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NO_CONTENT.value())));

        restTemplate.delete(url);

        wireMockServer.verify(deleteRequestedFor(urlEqualTo("/repositories/" + USERNAME + "/" + REPOSITORY_NAME)));
    }

    @Test
    public void getRepositoryDetails_DataCorrect_RepositoryDtoReturned() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(repository);

        wireMockServer.stubFor(get(urlEqualTo("/local/repositories/" + USERNAME + "/" + REPOSITORY_NAME))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        String urlLocal = String.format("/local/repositories/%s/%s", USERNAME, REPOSITORY_NAME);
        Repository result = restTemplate.getForObject(urlLocal, Repository.class);

        assertThat(result).isNotNull();
        assertEquals(REPOSITORY_NAME, result.getRepositoryName());
        assertEquals(USERNAME, result.getOwner());
        assertEquals(repository.getDescription(), result.getDescription());
        assertEquals(repository.getCloneUrl(), result.getCloneUrl());
        assertEquals(repository.getFullName(), result.getFullName());
        assertEquals(repository.getStars(), result.getStars());
        assertEquals(repository.getCreatedAt(), result.getCreatedAt());
    }
}