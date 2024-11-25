package com.example.demo.client.validation;

import com.example.demo.client.exception.RepositoryExistsException;
import com.example.demo.client.model.entity.Repository;
import com.example.demo.client.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositoryValidation {

    private final RepositoryRepository repositoryRepository;

    public void validateRepositoryExists(String owner, String repositoryName) {
        boolean repositoryExists = repositoryRepository.existsByOwnerAndRepositoryName(owner, repositoryName);
        if (repositoryExists) {
            throw new RepositoryExistsException(owner, repositoryName);
        }
    }

    public boolean isRepositoryNull(Repository repository) {
        return repository == null;
    }

}
