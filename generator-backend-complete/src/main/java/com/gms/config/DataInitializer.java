package com.gms.config;

import com.gms.entity.User;
import com.gms.enums.Role;
import com.gms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .fullName("System Administrator")
                .role(Role.ADMIN)
                .email("admin@generator-ms.com")
                .active(true)
                .build();
            
            userRepository.save(admin);
            System.out.println("✅ Default admin user created: admin / admin123");
        }
    }
}
