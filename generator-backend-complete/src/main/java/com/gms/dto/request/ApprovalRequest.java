package com.gms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalRequest {

    @NotNull(message = "Approval decision is required")
    private Boolean approved; // true = approve, false = reject

    @Min(value = 0, message = "Completion factor must be between 0 and 1")
    @Max(value = 1, message = "Completion factor must be between 0 and 1")
    private Double completionFactor; // 0.0 to 1.0

    @Min(value = 0, message = "Quality factor must be between 0 and 1")
    @Max(value = 1, message = "Quality factor must be between 0 and 1")
    private Double qualityFactor; // 0.0 to 1.0

    private String adminReviewNotes;
}
