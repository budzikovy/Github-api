package com.example.demo.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageDTO {

    private HttpStatus status;
    private String error;
    private String message;
    private Timestamp timestamp;

}
