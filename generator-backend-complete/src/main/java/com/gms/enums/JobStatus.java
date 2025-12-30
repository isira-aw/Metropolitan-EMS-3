package com.gms.enums;

import java.util.Arrays;
import java.util.List;

public enum JobStatus {
    PENDING,
    TRAVELING,
    STARTED,
    ON_HOLD,
    COMPLETED,
    CANCEL;
    
    public List<JobStatus> getAllowedTransitions() {
        return switch (this) {
            case PENDING -> Arrays.asList(TRAVELING, CANCEL);
            case TRAVELING -> Arrays.asList(STARTED, ON_HOLD, CANCEL);
            case STARTED -> Arrays.asList(ON_HOLD, COMPLETED, CANCEL);
            case ON_HOLD -> Arrays.asList(STARTED, CANCEL);
            case COMPLETED, CANCEL -> List.of();
        };
    }
    
    public boolean canTransitionTo(JobStatus newStatus) {
        return getAllowedTransitions().contains(newStatus);
    }
}
