package com.gms.service;

import com.gms.dto.request.UserRequest;
import com.gms.entity.User;
import com.gms.enums.Role;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Page<User> getAllUsers(Role role, Pageable pageable) {
        if (role != null) {
            return userRepository.findByRole(role, pageable);
        }
        return userRepository.findAll(pageable);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    @Transactional
    public User createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .role(request.getRole())
            .phone(request.getPhone())
            .email(request.getEmail())
            .active(true)
            .build();
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(Long id, UserRequest request) {
        User user = getUserById(id);
        
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }
}
