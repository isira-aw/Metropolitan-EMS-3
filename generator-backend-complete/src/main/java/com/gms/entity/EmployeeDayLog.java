package com.gms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_day_logs",
    uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "day_date"}),
    indexes = {
        @Index(name = "idx_daylog_employee", columnList = "employee_id, day_date")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDayLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @NotNull
    @Column(name = "day_date", nullable = false)
    private LocalDate dayDate;
    
    @Column(name = "day_start_time")
    private LocalDateTime dayStartTime;
    
    @Column(name = "day_end_time")
    private LocalDateTime dayEndTime;
    
    @Column(name = "morning_ot_minutes")
    @Builder.Default
    private Integer morningOtMinutes = 0;
    
    @Column(name = "evening_ot_minutes")
    @Builder.Default
    private Integer eveningOtMinutes = 0;
    
    @Column(name = "total_work_minutes")
    @Builder.Default
    private Integer totalWorkMinutes = 0;
    
    @Version
    private Long version;
}
