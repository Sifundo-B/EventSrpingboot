package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.enums.Role;
import com.TeamC.Eventiefy.user.User;
import com.TeamC.Eventiefy.repository.UserRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInit {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User adminUser = User.builder()
                    .firstName("Busisiwe")
                    .lastName("Eventiefy")
                    .email("busi@eventiefy.com")
                    .password(passwordEncoder.encode("admin123"))
                    .address("123 Admin Street")
                    .phoneNumber("1234567890")
                    .role(Role.Admin)
                    .build();
            userRepository.save(adminUser);
        }
    }
}
