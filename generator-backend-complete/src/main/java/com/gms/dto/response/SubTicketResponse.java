package com.gms.dto.response;

import com.gms.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubTicketResponse {
    private Long id;
    private String ticketNumber;
    private Long mainTicketId;
    private String mainTicketNumber;
    private Integer mainTicketWeight; // 1-5 stars
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
