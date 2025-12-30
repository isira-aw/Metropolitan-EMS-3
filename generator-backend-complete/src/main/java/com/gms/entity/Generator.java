package com.gms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "generators", indexes = {
    @Index(name = "idx_generator_name", columnList = "name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Generator {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Model is required")
    @Column(nullable = false, length = 100)
    private String model;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 50)
    private String capacity;
    
    @Column(name = "location_name", length = 200)
    private String locationName;
    
    @Column(name = "owner_email", length = 100)
    private String ownerEmail;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(columnDefinition = "TEXT")
    private String note;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
