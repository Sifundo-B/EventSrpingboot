package com.TeamC.Eventiefy.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Base response class for handling common response attributes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    /**
     * Indicates whether the operation was successful.
     */
    private boolean success;
    /**
     * The message related to the operation.
     */
    private String message;
}