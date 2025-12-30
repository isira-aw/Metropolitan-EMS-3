package com.gms.dto.response;

import com.gms.enums.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobCardResponse {
    private Long id;
    private Long mainTicketId;
    private String ticketNumber;
    private String ticketTitle;
    private GeneratorResponse generator;
    private JobStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer workMinutes;
    private Boolean approved;
    private String image;
    private LocalDateTime createdAt;
}
