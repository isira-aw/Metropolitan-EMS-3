package com.gms.controller;

import com.gms.dto.request.LoginRequest;
import com.gms.dto.request.RefreshTokenRequest;
import com.gms.dto.response.AuthResponse;
import com.gms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }
}
