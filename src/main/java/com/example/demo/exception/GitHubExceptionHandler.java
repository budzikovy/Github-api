package com.example.demo.exception;

import com.example.demo.model.dto.ErrorMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;
import java.time.Instant;

@ControllerAdvice
public class GitHubExceptionHandler {

    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleRepositoryNotFoundException(RepositoryNotFoundException exception) {
        ErrorMessageDTO bodyOfResponse = new ErrorMessageDTO(HttpStatus.NOT_FOUND, "Not Found", exception.getMessage(), Timestamp.from(Instant.now()));
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RepositoryNotFoundGitHubException.class)
    public ResponseEntity<ErrorMessageDTO> RepositoryNotFoundGitHubException(RepositoryNotFoundGitHubException exception) {
        ErrorMessageDTO bodyOfResponse = new ErrorMessageDTO(HttpStatus.NOT_FOUND, "Not Found", exception.getMessage(), Timestamp.from(Instant.now()));
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RepositoryExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleRepositoryExistsException(RepositoryExistsException exception) {
        ErrorMessageDTO bodyOfResponse = new ErrorMessageDTO(HttpStatus.CONFLICT, "Conflict", exception.getMessage(), Timestamp.from(Instant.now()));
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.CONFLICT);
    }

}
