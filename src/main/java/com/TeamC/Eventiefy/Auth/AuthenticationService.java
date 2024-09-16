package com.TeamC.Eventiefy.Auth;

import com.TeamC.Eventiefy.enums.Role;
import com.TeamC.Eventiefy.user.User;
import com.TeamC.Eventiefy.repository.UserRepo;
import com.TeamC.Eventiefy.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setImage(request.getImage());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepo.save(user);

        String token = jwtService.generateTokenWithId(user.getFirstName(), user.getLastName(), user.getRole().name(), user.getEmail(),user,user.getUserId());
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(token, refreshToken, "User registered successfully");
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateTokenWithId(user.getFirstName(), user.getLastName(), user.getRole().name(), user.getEmail(),user,user.getUserId());
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(token, refreshToken, "User authenticated successfully");
    }

    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepo.save(user);
    }
}
