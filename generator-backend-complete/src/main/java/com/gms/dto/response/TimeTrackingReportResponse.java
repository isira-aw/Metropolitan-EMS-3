package com.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeTrackingReportResponse {
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyWorkRecord> dailyRecords;
    private Integer totalWorkMinutes;
    private Integer totalMorningOtMinutes;
    private Integer totalEveningOtMinutes;
    private Integer totalOtMinutes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyWorkRecord {
        private LocalDate date;
        private LocalDateTime dayStartTime;
        private LocalDateTime dayEndTime;
        private Integer totalWorkMinutes;
        private Integer morningOtMinutes;
        private Integer eveningOtMinutes;
        private Boolean dayStarted;
        private Boolean dayEnded;
        private List<JobCardSummary> jobCards;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobCardSummary {
        private Long jobCardId;
        private String ticketNumber;
        private String generatorName;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer workMinutes;
    }
}
