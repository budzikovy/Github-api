package com.example.demo.client.exception;

public class RepositoryNotFoundException extends RuntimeException {
    public RepositoryNotFoundException(String repositoryName, String owner) {
        super(String.format("Repository named %s by %s not found in database.", repositoryName, owner));
    }
}
