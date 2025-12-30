package com.gms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_scores", indexes = {
    @Index(name = "idx_score_employee", columnList = "employee_id"),
    @Index(name = "idx_score_ticket", columnList = "main_ticket_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeScore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_ticket_id", nullable = false)
    private MainTicket mainTicket;
    
    @NotNull
    @Column(nullable = false, length = 10)
    private String weight;
    
    @NotNull
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = false)
    private User approvedBy;
    
    @CreationTimestamp
    @Column(name = "approved_at", nullable = false, updatable = false)
    private LocalDateTime approvedAt;
}
