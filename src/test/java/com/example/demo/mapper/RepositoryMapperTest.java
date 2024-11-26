package com.example.demo.mapper;

import com.example.demo.model.dto.GitHubRepository;
import com.example.demo.model.dto.Owner;
import com.example.demo.model.dto.RepositoryDto;
import com.example.demo.model.entity.Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RepositoryMapperTest {

    @Autowired
    private RepositoryMapper repositoryMapper;

    @Test
    void shouldMapRepositoryToDto() {

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

        RepositoryDto dto = repositoryMapper.toDto(repository);

        assertNotNull(dto);
        assertEquals(repository.getId(), dto.getId());
        assertEquals(repository.getFullName(), dto.getFullName());
        assertEquals(repository.getDescription(), dto.getDescription());
        assertEquals(repository.getCloneUrl(), dto.getCloneUrl());
        assertEquals(repository.getStars(), dto.getStars());
        assertEquals(repository.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void shouldMapGitHubRepositoryToRepository() {

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

        Repository repositoryResult = repositoryMapper.toRepository(gitHubRepository);

        assertNotNull(repositoryResult);
        assertEquals(gitHubRepository.getOwner().getLogin(), repositoryResult.getOwner());
        assertEquals(gitHubRepository.getRepositoryName(), repositoryResult.getRepositoryName());
        assertEquals(gitHubRepository.getDescription(), repositoryResult.getDescription());
        assertEquals(gitHubRepository.getCloneUrl(), repositoryResult.getCloneUrl());
        assertEquals(gitHubRepository.getStars(), repositoryResult.getStars());
        assertEquals(gitHubRepository.getCreatedAt(), repositoryResult.getCreatedAt());
    }

}
