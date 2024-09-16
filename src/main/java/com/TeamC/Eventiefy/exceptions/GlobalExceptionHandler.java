package com.TeamC.Eventiefy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


 // Global exception handler for the application.
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles RuntimeExceptions.
     * @param ex the RuntimeException
     * @param request the WebRequest
     * @return a ResponseEntity containing the error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Bad Request", ex.getMessage(), request.getDescription(false));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * Handles general Exceptions.
     * @param ex the Exception
     * @param request the WebRequest
     * @return a ResponseEntity containing the error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", ex.getMessage(), request.getDescription(false));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

     // Inner class representing the structure of error responses.
    public static class ErrorResponse {
        private String title;
        private String message;
        private String details;

        public ErrorResponse(String title, String message, String details) {
            this.title = title;
            this.message = message;
            this.details = details;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
