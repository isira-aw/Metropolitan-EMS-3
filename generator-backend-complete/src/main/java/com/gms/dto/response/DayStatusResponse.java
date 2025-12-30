package com.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class DayStatusResponse {
    private boolean dayStarted;
    private boolean dayEnded;
    private LocalDateTime dayStartTime;
    private LocalDateTime dayEndTime;
}
