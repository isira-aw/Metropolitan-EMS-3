package com.gms.dto.request;

import com.gms.enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class MainTicketRequest {

    // Ticket number is auto-generated, not required from user
    private String ticketNumber;

    @NotNull(message = "Generator ID is required")
    private Long generatorId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Weight is required")
    @jakarta.validation.constraints.Min(value = 1, message = "Weight must be between 1 and 5")
    @jakarta.validation.constraints.Max(value = 5, message = "Weight must be between 1 and 5")
    private Integer weight; // 1=*, 2=**, 3=***, 4=****, 5=*****

    private TicketStatus status;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @NotNull(message = "Scheduled time is required")
    private LocalTime scheduledTime;

    @NotNull(message = "At least one employee must be assigned")
    @Size(min = 1, max = 5, message = "Must assign between 1 and 5 employees")
    private List<Long> employeeIds;
}
