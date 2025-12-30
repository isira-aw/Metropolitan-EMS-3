package com.gms.config;

import com.gms.entity.User;
import com.gms.enums.Role;
import com.gms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            try {
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
                    log.info("✅ Default admin user created: admin / admin123");
                    System.out.println("\n========================================");
                    System.out.println("✅ DEFAULT ADMIN USER CREATED");
                    System.out.println("   Username: admin");
                    System.out.println("   Password: admin123");
                    System.out.println("========================================\n");
                } else {
                    log.info("ℹ️ Admin user already exists");
                }
            } catch (Exception e) {
                log.error("❌ Failed to create default admin user", e);
                throw e;
            }
        };
    }
}