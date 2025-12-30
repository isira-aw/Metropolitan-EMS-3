package com.gms.entity;

import com.gms.enums.JobStatus;
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
@Table(name = "job_status_logs", indexes = {
    @Index(name = "idx_statuslog_jobcard", columnList = "mini_job_card_id"),
    @Index(name = "idx_statuslog_logged_at", columnList = "logged_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobStatusLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mini_job_card_id", nullable = false)
    private MiniJobCard miniJobCard;
    
    @Column(length = 100)
    private String generator;
    
    @Column(name = "employee_email", length = 100)
    private String employeeEmail;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "pass_status", length = 20)
    private JobStatus passStatus;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private JobStatus newStatus;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @CreationTimestamp
    @Column(name = "logged_at", nullable = false, updatable = false)
    private LocalDateTime loggedAt;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;
}
