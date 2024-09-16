package com.TeamC.Eventiefy.Auth;

import com.TeamC.Eventiefy.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

 // DTO for handling user registration requests.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    /**
     * The first name of the user.
     * Should not be blank.
     */
    @NotBlank(message = "First name is required")
    private String firstName;
    /**
     * The last name of the user.
     * Should not be blank.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;
    /**
     * The phone number of the user.
     * Should not be blank.
     */
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    /**
     * The address of the user.
     * Should not be blank.
     */
    @NotBlank(message = "Address is required")
    private String address;
    /**
     * The email of the user.
     * Should be a valid email format and not blank.
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    /**
     * The password of the user.
     * Should be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.
     * Should not be blank.
     */
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character")
    @NotBlank(message = "Password is required")
    private String password;
    /**
     * The confirmation password of the user.
     * Should match the password and not be blank.
     */
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    /**
     * The profile image of the user.
     * Optional field.
     */
    private String image;
    /**
     * The role of the user.
     * Optional field.
     */
    private Role role;
}