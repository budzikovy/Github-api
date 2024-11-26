package com.example.demo.validation;

import com.example.demo.exception.RepositoryExistsException;
import com.example.demo.model.entity.Repository;
import com.example.demo.repository.RepositoryRepository;
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
