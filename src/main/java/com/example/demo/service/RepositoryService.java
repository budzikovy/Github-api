package com.example.demo.service;

import com.example.demo.client.GitHubClient;
import com.example.demo.model.dto.GitHubRepository;
import com.example.demo.model.entity.Repository;
import com.example.demo.model.dto.RepositoryDto;
import com.example.demo.exception.RepositoryNotFoundException;
import com.example.demo.exception.RepositoryNotFoundGitHubException;
import com.example.demo.mapper.RepositoryMapper;
import com.example.demo.repository.RepositoryRepository;
import com.example.demo.validation.RepositoryValidation;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RepositoryService {

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

    @Transactional
    public RepositoryDto saveRepository(String owner, String repositoryName, Repository repository) {
        repositoryValidation.validateRepositoryExists(owner, repositoryName);

        if (repositoryValidation.isRepositoryNull(repository)) {
            repository = getDetails(owner, repositoryName);
        }

        repositoryRepository.save(repository);
        return repositoryMapper.toDto(repository);
    }


    public RepositoryDto getRepositoryDetails(String owner, String repositoryName) {
        Repository repositoryDetails = repositoryRepository.findByOwnerAndRepositoryName(owner, repositoryName)
                .orElseThrow(() -> new RepositoryNotFoundException(repositoryName, owner));
        repositoryRepository.findByOwnerAndRepositoryName(owner, repositoryName);
        return repositoryMapper.toDto(repositoryDetails);
    }

    @Transactional
    public RepositoryDto deleteRepository(String owner, String repositoryName) {
        Repository deletedRepository = repositoryRepository.findByOwnerAndRepositoryName(owner, repositoryName)
                .orElseThrow(() -> new RepositoryNotFoundException(repositoryName, owner));
        repositoryRepository.delete(deletedRepository);
        return repositoryMapper.toDto(deletedRepository);
    }

    @Transactional
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
