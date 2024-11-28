package com.example.demo.exception;

public class RepositoryNotFoundGitHubException extends RuntimeException {
    public RepositoryNotFoundGitHubException(String repositoryName, String owner) {
        super(String.format("GitHub api cannot find repository named %s by %s.", repositoryName, owner));
    }
}
