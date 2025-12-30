package com.gms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_assignments", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"main_ticket_id", "employee_id"}),
    indexes = {
        @Index(name = "idx_assignment_ticket", columnList = "main_ticket_id"),
        @Index(name = "idx_assignment_employee", columnList = "employee_id")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssignment {
    
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
    
    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;
}
