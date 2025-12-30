package com.gms.dto.response;

import com.gms.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private String weight;
    private TicketStatus status;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
