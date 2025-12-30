package com.gms.dto.response;

import com.gms.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class MainTicketResponse {
    private Long id;
    private String ticketNumber;
    private Long generatorId;
    private String generatorName;
    private String generatorModel;
    private String title;
    private String description;
    private Integer weight; // 1-5 stars (* to *****)
    private String weightDisplay; // Display as stars: "*****"
    private TicketStatus status;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private List<SubTicketResponse> subTickets;
    private Integer totalAssignments;
    private Integer completedAssignments;
}
