package com.gms.dto.request;

import com.gms.enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MainTicketRequest {

    @NotBlank(message = "Ticket number is required")
    private String ticketNumber;

    @NotNull(message = "Generator ID is required")
    private Long generatorId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String weight;

    private TicketStatus status;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @NotNull(message = "Scheduled time is required")
    private LocalTime scheduledTime;
}
