package com.gms.dto.request;

import com.gms.enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "New status is required")
    private JobStatus newStatus;
    
    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;
    
    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;
}
