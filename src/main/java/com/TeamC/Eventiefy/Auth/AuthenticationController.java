package com.TeamC.Eventiefy.Auth;

import com.TeamC.Eventiefy.config.JwtService;
import com.TeamC.Eventiefy.entity.PaymentMethod;
import com.TeamC.Eventiefy.enums.Role;
import com.TeamC.Eventiefy.services.PasswordResetService;
import com.TeamC.Eventiefy.services.UserServiceImpl;
import com.TeamC.Eventiefy.user.User;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthenticationController {

    private final AuthenticationService service;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserServiceImpl userService;
    private final Cloudinary cloudinary;
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/send-reset-email")
    public ResponseEntity<Map<String, String>> sendResetEmail(@RequestBody EmailRequest emailRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            passwordResetService.sendPasswordResetEmail(emailRequest.getEmail());
            response.put("message", "Password reset email sent successfully!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "Failed to send password reset email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetRequest resetRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            passwordResetService.resetPassword(resetRequest.getToken(), resetRequest.getNewPassword());
            response.put("message", "Password reset successfully!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Gets the preferred payment method for a user.
     * @param userId the ID of the user
     * @return the preferred payment method or a not found response
     */
    @GetMapping("/{userId}/preferred-payment-method")
    public ResponseEntity<PaymentMethod> getUserPreferredPaymentMethod(@PathVariable Long userId) {
        Optional<PaymentMethod> paymentMethod = userService.getUserPreferredPaymentMethod(userId);
        return paymentMethod.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates the preferred payment method for a user.
     * @param userId the ID of the user
     * @param paymentMethodId the ID of the new preferred payment method
     * @return a response indicating the result of the operation
     */
    @PutMapping("/{userId}/preferred-payment-method/{paymentMethodId}")
    public ResponseEntity<Void> updateUserPreferredPaymentMethod(
            @PathVariable Long userId, @PathVariable Long paymentMethodId) {
        try {
            userService.updateUserPreferredPaymentMethod(userId, paymentMethodId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    /**
     * Refreshes the JWT token.
     * @param request the refresh token request
     * @return a response with the new token or an error response
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String username = jwtService.extractUsername(request.getRefreshToken());
            String token = jwtService.generateTokenFromUsername(username);
            return ResponseEntity.ok(new AuthenticationResponse(token, request.getRefreshToken(), "Token refreshed successfully"));
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(new ErrorResponse("Refresh token expired", "Refresh token error"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), "Token error"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Registers a new user.
     * @param request the registration request
     * @return a response indicating the result of the registration
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Role role = request.getRole(); // Use the Role enum directly
            AuthenticationResponse response = service.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorResponse("Invalid role: " + request.getRole(), "Registration error"), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), "Registration error"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Authenticates a user.
     * @param request the authentication request
     * @return a response indicating the result of the authentication
     */
    @PostMapping("/authenticate")
    public ResponseEntity<BaseResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = service.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage(), "Authentication error"), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Gets all users.
     * @return a list of all users
     */
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    /**
     * Gets a user by ID.
     * @param id the ID of the user
     * @return the user or a not found response
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates user information.
     * @param id the ID of the user
     * @param userDetails the updated user details
     * @return a response indicating the result of the update
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestPart("userDetails") User userDetails,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setAddress(userDetails.getAddress());
            user.setEmail(userDetails.getEmail());

            if (image != null && !image.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("url");
                user.setImage(imageUrl);
            }

            userService.save(user);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            // Log the exception with a stack trace for better debugging
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception with a stack trace for better debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }


    /**
     * Deletes a user by ID.
     * @param userId the ID of the user to be deleted
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userService.delete(user);
    }

    /**
     * Finds a user by email.
     * @param email the email of the user
     * @return the user with the specified email
     */
    @GetMapping("/email/{email}")
    public Optional<User> findByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    /**
     * Finds a user by email and role.
     * @param email the email of the user
     * @param role the role of the user
     * @return the user with the specified email and role
     */
    @GetMapping("/email-role")
    public Optional<User> findByEmailAndRole(@RequestParam String email, @RequestParam Role role) {
        return userService.findByEmailAndRole(email, role);
    }

    /**
     * Finds a user by reset token.
     * @param resetToken the reset token of the user
     * @return the user with the specified reset token
     */
    @GetMapping("/reset-token/{resetToken}")
    public Optional<User> findByResetToken(@PathVariable String resetToken) {
        return userService.findByResetToken(resetToken);
    }
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Long>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token.substring(7)); // Remove "Bearer " prefix
            User user = userService.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
            Map<String, Long> response = new HashMap<>();
            response.put("id", user.getUserId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}