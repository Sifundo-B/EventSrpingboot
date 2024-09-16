package com.TeamC.Eventiefy.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

 // Global exception handler for file upload exceptions.
@ControllerAdvice
public class FileUploadExceptionAdvice {
    /**
     * Handles exceptions when the uploaded file exceeds the maximum allowed size.
     * @param exc the exception thrown when the file is too large
     * @return a ResponseEntity with an error message and HTTP status EXPECTATION_FAILED
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("File too large!");
    }
}
