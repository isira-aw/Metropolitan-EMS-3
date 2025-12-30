package com.gms.dto.response;

import com.gms.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class SubTicketResponse {
    private Long id;
    private String ticketNumber;
    private Long mainTicketId;
    private String mainTicketNumber;

    // Main ticket details (essential for employees to know what work to do)
    private String mainTicketTitle;
    private String mainTicketDescription;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Integer mainTicketWeight; // 1-5 stars

    // Generator information
    private Long generatorId;
    private String generatorName;
    private String generatorModel;
    private String generatorLocation;

    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private TicketStatus status;
    private String notes;

    // Scoring fields
    private Double completionFactor;
    private Double qualityFactor;
    private Double score;

    // Approval fields
    private Boolean approved;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String adminReviewNotes;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
