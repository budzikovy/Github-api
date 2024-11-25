package com.example.demo.client.service;

import com.example.demo.client.GitHubClient;
import com.example.demo.client.model.dto.GitHubRepository;
import com.example.demo.client.model.entity.Repository;
import com.example.demo.client.model.dto.RepositoryDto;
import com.example.demo.client.exception.RepositoryNotFoundException;
import com.example.demo.client.exception.RepositoryNotFoundGitHubException;
import com.example.demo.client.mapper.RepositoryMapper;
import com.example.demo.client.repository.RepositoryRepository;
import com.example.demo.client.validation.RepositoryValidation;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResponseService {

    private final GitHubClient gitHubClient;
    private final RepositoryMapper repositoryMapper;
    private final RepositoryRepository repositoryRepository;
    private final RepositoryValidation repositoryValidation;

    public Repository getDetails(String owner, String repositoryName) {
        try {
            GitHubRepository gitHubRepository = gitHubClient.getDetails(owner, repositoryName);
            return repositoryMapper.toRepository(gitHubRepository);
        } catch (FeignException.NotFound exception) {
            throw new RepositoryNotFoundGitHubException(repositoryName, owner);
        }
    }

    public RepositoryDto saveRepository(String owner, String repositoryName, Repository repository) {
        repositoryValidation.validateRepositoryExists(owner, repositoryName);

        if (repositoryValidation.isRepositoryNull(repository)) {
            repository = getDetails(owner, repositoryName);
        }

        repository.setOwner(owner);
        repository.setRepositoryName(repositoryName);
        repositoryRepository.save(repository);
        return repositoryMapper.toDto(repository);
    }


    public RepositoryDto getRepositoryDetails(String owner, String repositoryName) {
        Repository repositoryDetails = repositoryRepository.findByOwnerAndRepositoryName(owner, repositoryName)
                .orElseThrow(() -> new RepositoryNotFoundException(repositoryName, owner));
        repositoryRepository.findByOwnerAndRepositoryName(owner, repositoryName);
        return repositoryMapper.toDto(repositoryDetails);
    }

    public RepositoryDto deleteRepository(String owner, String repositoryName) {
        Repository deletedRepository = repositoryRepository.findByOwnerAndRepositoryName(owner, repositoryName)
                .orElseThrow(() -> new RepositoryNotFoundException(repositoryName, owner));
        repositoryRepository.delete(deletedRepository);
        return repositoryMapper.toDto(deletedRepository);
    }

    public RepositoryDto editRepository(String owner, String repositoryName, Repository updatedRepository) {
        Repository editRepository = repositoryRepository.findByOwnerAndRepositoryName(owner, repositoryName)
                .orElseThrow(() -> new RepositoryNotFoundException(repositoryName, owner));

        editRepository.setFullName(updatedRepository.getFullName());
        editRepository.setDescription(updatedRepository.getDescription());
        editRepository.setCloneUrl(updatedRepository.getCloneUrl());
        editRepository.setStars(updatedRepository.getStars());
        editRepository.setCreatedAt(updatedRepository.getCreatedAt());

        Repository editedRepository = repositoryRepository.save(editRepository);
        return repositoryMapper.toDto(editedRepository);
    }
}
