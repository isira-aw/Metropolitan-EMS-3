#!/usr/bin/env python3
import os
from pathlib import Path

def write_file(path, content):
    Path(path).parent.mkdir(parents=True, exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content.strip() + '\n')

base = "/home/claude/generator-backend-complete/src/main/java/com/gms"

# Security Config
write_file(f"{base}/config/SecurityConfig.java", """package com.gms.config;

import com.gms.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/employee/**").hasAuthority("EMPLOYEE")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}""")

print("Created SecurityConfig")

# Request DTOs - showing key ones
write_file(f"{base}/dto/request/LoginRequest.java", """package com.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}""")

write_file(f"{base}/dto/request/RefreshTokenRequest.java", """package com.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}""")

write_file(f"{base}/dto/request/UserRequest.java", """package com.gms.dto.request;

import com.gms.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    private String password;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotNull(message = "Role is required")
    private Role role;
    
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
}""")

write_file(f"{base}/dto/request/StatusUpdateRequest.java", """package com.gms.dto.request;

import com.gms.enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "New status is required")
    private JobStatus newStatus;
    
    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;
    
    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;
}""")

# Response DTOs
write_file(f"{base}/dto/response/AuthResponse.java", """package com.gms.dto.response;

import com.gms.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long userId;
    private String username;
    private String fullName;
    private Role role;
}""")

write_file(f"{base}/dto/response/DayStatusResponse.java", """package com.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class DayStatusResponse {
    private boolean dayStarted;
    private boolean dayEnded;
    private LocalDateTime dayStartTime;
    private LocalDateTime dayEndTime;
}""")

write_file(f"{base}/dto/response/JobCardResponse.java", """package com.gms.dto.response;

import com.gms.enums.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobCardResponse {
    private Long id;
    private Long mainTicketId;
    private String ticketNumber;
    private String ticketTitle;
    private GeneratorResponse generator;
    private JobStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer workMinutes;
    private Boolean approved;
    private String image;
    private LocalDateTime createdAt;
}""")

write_file(f"{base}/dto/response/GeneratorResponse.java", """package com.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class GeneratorResponse {
    private Long id;
    private String model;
    private String name;
    private String capacity;
    private String locationName;
    private String ownerEmail;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String note;
    private LocalDateTime createdAt;
}""")

print("Created DTOs")

