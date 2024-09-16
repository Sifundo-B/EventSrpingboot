package com.TeamC.Eventiefy.Auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * Error response class for handling error-specific response attributes.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ErrorResponse extends BaseResponse {
    /**
     * Detailed information about the error.
     */
    private String details;
    /**
     * Constructs an ErrorResponse with the specified message and details.
     *
     * @param message the error message
     * @param details the detailed information about the error
     */
    public ErrorResponse(String message, String details) {
        super(false, message);
        this.details = details;
    }
}