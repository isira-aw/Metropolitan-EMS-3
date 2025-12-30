package com.gms.entity;

import com.gms.enums.JobStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mini_job_cards", indexes = {
    @Index(name = "idx_jobcard_employee", columnList = "employee_id"),
    @Index(name = "idx_jobcard_status", columnList = "status"),
    @Index(name = "idx_jobcard_ticket", columnList = "main_ticket_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniJobCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_ticket_id", nullable = false)
    private MainTicket mainTicket;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean approved = false;
    
    @Column(name = "work_minutes")
    @Builder.Default
    private Integer workMinutes = 0;
    
    @Column(columnDefinition = "TEXT")
    private String image;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Version
    private Long version;
}
