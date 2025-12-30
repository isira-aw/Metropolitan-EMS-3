package com.gms.entity;

import com.gms.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_role", columnList = "role")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    @Column(length = 20)
    private String phone;
    
    @Email(message = "Invalid email format")
    @Column(unique = true, length = 100)
    private String email;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "refresh_token", length = 500)
    private String refreshToken;
}
