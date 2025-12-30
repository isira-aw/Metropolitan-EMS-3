package com.gms.dto.response;

import com.gms.enums.JobCardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTTrackingReportResponse {
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDaysWorked;
    private Integer totalWorkMinutes;
    private Integer totalMorningOtMinutes;
    private Integer totalEveningOtMinutes;
    private Integer totalOtMinutes;
    private List<DailyOTRecord> dailyOTRecords;
    private Map<JobCardType, Integer> workMinutesByJobType;
    private PerformanceAnalysis performanceAnalysis;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyOTRecord {
        private LocalDate date;
        private String dayOfWeek;
        private Integer totalWorkMinutes;
        private Integer morningOtMinutes;
        private Integer eveningOtMinutes;
        private Integer regularWorkMinutes;
        private List<GeneratorWork> generatorWorks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratorWork {
        private String generatorName;
        private String generatorModel;
        private JobCardType jobCardType;
        private Integer workMinutes;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceAnalysis {
        private Double averageWorkMinutesPerDay;
        private Double averageOTMinutesPerDay;
        private Integer totalJobsCompleted;
        private Integer totalJobsInProgress;
        private Integer totalJobsCancelled;
        private Double jobCompletionRate;
        private String mostWorkedJobType;
        private Integer mostWorkedJobTypeMinutes;
    }
}
