package com.TeamC.Eventiefy.Auth;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling authentication responses.
 */
@Data
@NoArgsConstructor
public class AuthenticationResponse extends BaseResponse {
    /**
     * The JWT token.
     */
    private String token;
    /**
     * The refresh token.
     */
    private String refreshToken;
    /**
     * Constructs an AuthenticationResponse with the specified token, refresh token, and message.
     *
     * @param token the JWT token
     * @param refreshToken the refresh token
     * @param message the message
     */
    public AuthenticationResponse(String token, String refreshToken, String message) {
        super(true, message);
        this.token = token;
        this.refreshToken = refreshToken;
    }
    /**
     * Constructs an AuthenticationResponse with the specified success status, message, token, and refresh token.
     *
     * @param success the success status
     * @param message the message
     * @param token the JWT token
     * @param refreshToken the refresh token
     */
    public AuthenticationResponse(boolean success, String message, String token, String refreshToken) {
        super(success, message);
        this.token = token;
        this.refreshToken = refreshToken;
    }
}