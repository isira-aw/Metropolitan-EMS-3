package com.gms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobCardApprovalRequest {

    @NotNull(message = "Approval decision is required")
    private Boolean approved; // true = approve, false = reject

    private String reviewNotes;
}
