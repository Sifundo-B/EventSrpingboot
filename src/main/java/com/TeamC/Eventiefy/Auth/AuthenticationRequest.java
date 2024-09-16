package com.TeamC.Eventiefy.Auth;

import com.TeamC.Eventiefy.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for handling authentication requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationRequest {

    /**
     * The email of the user.
     * Should be a valid email format and not blank.
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * The password of the user.
     * Should not be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * The role of the user.
     * Optional field.
     */
    private Role role;
}