package com.gms.service;

import com.gms.dto.response.OTTrackingReportResponse;
import com.gms.dto.response.TimeTrackingReportResponse;
import com.gms.entity.EmployeeDayLog;
import com.gms.entity.JobStatusLog;
import com.gms.entity.MiniJobCard;
import com.gms.entity.User;
import com.gms.enums.JobCardType;
import com.gms.enums.JobStatus;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.EmployeeDayLogRepository;
import com.gms.repository.MiniJobCardRepository;
import com.gms.repository.JobStatusLogRepository;
import com.gms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final UserRepository userRepository;
    private final EmployeeDayLogRepository dayLogRepository;
    private final MiniJobCardRepository jobCardRepository;
    private final JobStatusLogRepository statusLogRepository;

    public TimeTrackingReportResponse generateTimeTrackingReport(Long employeeId, LocalDate startDate, LocalDate endDate) {
        // Validate employee
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Validate date range (max 90 days)
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 90) {
            throw new IllegalArgumentException("Date range cannot exceed 90 days");
        }

        // Get all day logs for the period
        List<EmployeeDayLog> dayLogs = dayLogRepository.findByEmployeeIdAndDayDateBetween(
            employeeId, startDate, endDate
        );

        // Get all job cards for the period
        List<MiniJobCard> jobCards = jobCardRepository.findByEmployeeIdAndCreatedAtBetween(
            employeeId,
            startDate.atStartOfDay(),
            endDate.atTime(23, 59, 59)
        );

        // Build daily records
        List<TimeTrackingReportResponse.DailyWorkRecord> dailyRecords = new ArrayList<>();
        int totalWorkMinutes = 0;
        int totalMorningOT = 0;
        int totalEveningOT = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            // Find day log for this date
            Optional<EmployeeDayLog> dayLogOpt = dayLogs.stream()
                .filter(dl -> dl.getDayDate().equals(currentDate))
                .findFirst();

            // Find job cards for this date
            List<MiniJobCard> dateJobCards = jobCards.stream()
                .filter(jc -> jc.getCreatedAt().toLocalDate().equals(currentDate) ||
                             (jc.getStartTime() != null && jc.getStartTime().toLocalDate().equals(currentDate)))
                .collect(Collectors.toList());

            // Build job card summaries
            List<TimeTrackingReportResponse.JobCardSummary> jobCardSummaries = dateJobCards.stream()
                .map(jc -> TimeTrackingReportResponse.JobCardSummary.builder()
                    .jobCardId(jc.getId())
                    .ticketNumber(jc.getMainTicket().getTicketNumber())
                    .generatorName(jc.getMainTicket().getGenerator().getName())
                    .status(jc.getStatus().toString())
                    .startTime(jc.getStartTime())
                    .endTime(jc.getEndTime())
                    .workMinutes(jc.getWorkMinutes())
                    .build())
                .collect(Collectors.toList());

            if (dayLogOpt.isPresent()) {
                EmployeeDayLog dayLog = dayLogOpt.get();
                totalWorkMinutes += dayLog.getTotalWorkMinutes();
                totalMorningOT += dayLog.getMorningOtMinutes();
                totalEveningOT += dayLog.getEveningOtMinutes();

                dailyRecords.add(TimeTrackingReportResponse.DailyWorkRecord.builder()
                    .date(currentDate)
                    .dayStartTime(dayLog.getDayStartTime())
                    .dayEndTime(dayLog.getDayEndTime())
                    .totalWorkMinutes(dayLog.getTotalWorkMinutes())
                    .morningOtMinutes(dayLog.getMorningOtMinutes())
                    .eveningOtMinutes(dayLog.getEveningOtMinutes())
                    .dayStarted(dayLog.getDayStartTime() != null)
                    .dayEnded(dayLog.getDayEndTime() != null)
                    .jobCards(jobCardSummaries)
                    .build());
            } else {
                // No day log for this date
                dailyRecords.add(TimeTrackingReportResponse.DailyWorkRecord.builder()
                    .date(currentDate)
                    .dayStarted(false)
                    .dayEnded(false)
                    .totalWorkMinutes(0)
                    .morningOtMinutes(0)
                    .eveningOtMinutes(0)
                    .jobCards(jobCardSummaries)
                    .build());
            }
        }

        return TimeTrackingReportResponse.builder()
            .employeeId(employeeId)
            .employeeName(employee.getFullName())
            .employeeEmail(employee.getEmail())
            .startDate(startDate)
            .endDate(endDate)
            .dailyRecords(dailyRecords)
            .totalWorkMinutes(totalWorkMinutes)
            .totalMorningOtMinutes(totalMorningOT)
            .totalEveningOtMinutes(totalEveningOT)
            .totalOtMinutes(totalMorningOT + totalEveningOT)
            .build();
    }

    public OTTrackingReportResponse generateOTTrackingReport(Long employeeId, LocalDate startDate, LocalDate endDate) {
        // Validate employee
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Validate date range (max 90 days)
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 90) {
            throw new IllegalArgumentException("Date range cannot exceed 90 days");
        }

        // Get all day logs for the period
        List<EmployeeDayLog> dayLogs = dayLogRepository.findByEmployeeIdAndDayDateBetween(
            employeeId, startDate, endDate
        );

        // Get all job cards for the period
        List<MiniJobCard> jobCards = jobCardRepository.findByEmployeeIdAndCreatedAtBetween(
            employeeId,
            startDate.atStartOfDay(),
            endDate.atTime(23, 59, 59)
        );

        // Calculate totals
        int totalWorkMinutes = dayLogs.stream()
            .mapToInt(EmployeeDayLog::getTotalWorkMinutes)
            .sum();

        int totalMorningOT = dayLogs.stream()
            .mapToInt(EmployeeDayLog::getMorningOtMinutes)
            .sum();

        int totalEveningOT = dayLogs.stream()
            .mapToInt(EmployeeDayLog::getEveningOtMinutes)
            .sum();

        // Build daily OT records
        List<OTTrackingReportResponse.DailyOTRecord> dailyOTRecords = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            // Find day log for this date
            Optional<EmployeeDayLog> dayLogOpt = dayLogs.stream()
                .filter(dl -> dl.getDayDate().equals(currentDate))
                .findFirst();

            if (dayLogOpt.isEmpty()) {
                continue; // Skip days where employee didn't work
            }

            EmployeeDayLog dayLog = dayLogOpt.get();

            // Find job cards for this date
            List<MiniJobCard> dateJobCards = jobCards.stream()
                .filter(jc -> jc.getCreatedAt().toLocalDate().equals(currentDate) ||
                             (jc.getStartTime() != null && jc.getStartTime().toLocalDate().equals(currentDate)))
                .collect(Collectors.toList());

            // Build generator works
            List<OTTrackingReportResponse.GeneratorWork> generatorWorks = dateJobCards.stream()
                .map(jc -> OTTrackingReportResponse.GeneratorWork.builder()
                    .generatorName(jc.getMainTicket().getGenerator().getName())
                    .generatorModel(jc.getMainTicket().getGenerator().getModel())
                    .jobCardType(jc.getMainTicket().getJobCardType())
                    .workMinutes(jc.getWorkMinutes())
                    .status(jc.getStatus().toString())
                    .build())
                .collect(Collectors.toList());

            // Calculate regular work minutes (excluding OT)
            int regularWorkMinutes = dayLog.getTotalWorkMinutes() -
                                    dayLog.getMorningOtMinutes() -
                                    dayLog.getEveningOtMinutes();

            dailyOTRecords.add(OTTrackingReportResponse.DailyOTRecord.builder()
                .date(currentDate)
                .dayOfWeek(currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .totalWorkMinutes(dayLog.getTotalWorkMinutes())
                .morningOtMinutes(dayLog.getMorningOtMinutes())
                .eveningOtMinutes(dayLog.getEveningOtMinutes())
                .regularWorkMinutes(regularWorkMinutes)
                .generatorWorks(generatorWorks)
                .build());
        }

        // Calculate work minutes by job type
        Map<JobCardType, Integer> workMinutesByJobType = jobCards.stream()
            .filter(jc -> jc.getMainTicket().getJobCardType() != null)
            .collect(Collectors.groupingBy(
                jc -> jc.getMainTicket().getJobCardType(),
                Collectors.summingInt(MiniJobCard::getWorkMinutes)
            ));

        // Calculate performance analysis
        long completedJobs = jobCards.stream()
            .filter(jc -> jc.getStatus() == JobStatus.COMPLETED)
            .count();

        long inProgressJobs = jobCards.stream()
            .filter(jc -> jc.getStatus() == JobStatus.STARTED ||
                         jc.getStatus() == JobStatus.TRAVELING ||
                         jc.getStatus() == JobStatus.ON_HOLD)
            .count();

        long cancelledJobs = jobCards.stream()
            .filter(jc -> jc.getStatus() == JobStatus.CANCEL)
            .count();

        double completionRate = jobCards.isEmpty() ? 0.0 :
            (completedJobs * 100.0) / jobCards.size();

        // Find most worked job type
        Map.Entry<JobCardType, Integer> mostWorkedEntry = workMinutesByJobType.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

        OTTrackingReportResponse.PerformanceAnalysis performanceAnalysis =
            OTTrackingReportResponse.PerformanceAnalysis.builder()
                .averageWorkMinutesPerDay(dailyOTRecords.isEmpty() ? 0.0 :
                    totalWorkMinutes / (double) dailyOTRecords.size())
                .averageOTMinutesPerDay(dailyOTRecords.isEmpty() ? 0.0 :
                    (totalMorningOT + totalEveningOT) / (double) dailyOTRecords.size())
                .totalJobsCompleted((int) completedJobs)
                .totalJobsInProgress((int) inProgressJobs)
                .totalJobsCancelled((int) cancelledJobs)
                .jobCompletionRate(completionRate)
                .mostWorkedJobType(mostWorkedEntry != null ? mostWorkedEntry.getKey().toString() : "N/A")
                .mostWorkedJobTypeMinutes(mostWorkedEntry != null ? mostWorkedEntry.getValue() : 0)
                .build();

        return OTTrackingReportResponse.builder()
            .employeeId(employeeId)
            .employeeName(employee.getFullName())
            .employeeEmail(employee.getEmail())
            .startDate(startDate)
            .endDate(endDate)
            .totalDaysWorked(dailyOTRecords.size())
            .totalWorkMinutes(totalWorkMinutes)
            .totalMorningOtMinutes(totalMorningOT)
            .totalEveningOtMinutes(totalEveningOT)
            .totalOtMinutes(totalMorningOT + totalEveningOT)
            .dailyOTRecords(dailyOTRecords)
            .workMinutesByJobType(workMinutesByJobType)
            .performanceAnalysis(performanceAnalysis)
            .build();
    }
}
