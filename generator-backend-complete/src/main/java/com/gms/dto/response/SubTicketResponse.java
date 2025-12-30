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
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private TicketStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
