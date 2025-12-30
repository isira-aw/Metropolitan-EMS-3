package com.gms.entity;

import com.gms.enums.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "main_tickets", indexes = {
    @Index(name = "idx_ticket_status", columnList = "status"),
    @Index(name = "idx_ticket_scheduled", columnList = "scheduled_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Ticket number is required")
    @Column(name = "ticket_number", unique = true, nullable = false, length = 50)
    private String ticketNumber;
    
    @NotNull(message = "Generator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generator_id", nullable = false)
    private Generator generator;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 10)
    private String weight;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketStatus status = TicketStatus.CREATED;
    
    @NotNull(message = "Scheduled date is required")
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;
    
    @NotNull(message = "Scheduled time is required")
    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;
    
    @NotNull(message = "Creator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
