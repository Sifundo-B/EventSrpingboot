package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.TeamC.Eventiefy.user.User;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void sendPasswordResetEmail(String email) throws IOException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("No user found with this email address");
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String resetUrl = "http://localhost:4200/reset-password?token=" + token;
        String message = "Click the following link to reset your password: " + resetUrl;

        emailService.sendEmail(email, "Password Reset Request", message);
    }

    public void resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("Invalid password reset token");
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }
}
