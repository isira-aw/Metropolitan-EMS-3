package com.gms.service;

import com.gms.dto.request.LoginRequest;
import com.gms.dto.response.AuthResponse;
import com.gms.entity.User;
import com.gms.exception.UnauthorizedException;
import com.gms.repository.UserRepository;
import com.gms.security.JwtTokenProvider;
import com.gms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal.getId());
        
        User user = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .userId(userPrincipal.getId())
            .username(userPrincipal.getUsername())
            .fullName(user.getFullName())
            .role(userPrincipal.getRole())
            .build();
    }
    
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        
        Long userId = tokenProvider.getUserIdFromJWT(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new UnauthorizedException("Refresh token does not match");
        }
        
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, userPrincipal.getAuthorities()
        );
        
        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        
        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .role(user.getRole())
            .build();
    }
    
    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
