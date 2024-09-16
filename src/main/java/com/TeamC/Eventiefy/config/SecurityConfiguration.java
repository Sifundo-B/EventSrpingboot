package com.TeamC.Eventiefy.config;

import com.TeamC.Eventiefy.services.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserServiceImpl userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll() // Allow WebSocket access
                        .requestMatchers("/api/v1/cloudinary").permitAll()
                        .requestMatchers("/api/preferences/**").permitAll()
                        .requestMatchers("/api/payment-methods/**").permitAll()
                        .requestMatchers("/api/transactions").permitAll()
                        .requestMatchers("/api/v1/paypal/**").permitAll()
                        .requestMatchers("/api/v1/analytics/**").permitAll()
                        .requestMatchers("//api/v1/tickets/**").permitAll()
                                .requestMatchers("/api/v1/ticketHistory/**").permitAll()
                .requestMatchers("/api/subscriptions/**").permitAll()
                        .requestMatchers("/api/features/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/eventCategories/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        return authManagerBuilder.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }
}
