package com.example.demo.client.exception;

public class RepositoryExistsException extends RuntimeException {
    public RepositoryExistsException(String repositoryName, String owner) {
        super(String.format("Repository named %s by %s already exists in database.", repositoryName, owner));
    }
}
