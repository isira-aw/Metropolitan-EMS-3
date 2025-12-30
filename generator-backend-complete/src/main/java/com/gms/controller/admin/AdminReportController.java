package com.gms.controller.admin;

import com.gms.dto.response.OTTrackingReportResponse;
import com.gms.dto.response.TimeTrackingReportResponse;
import com.gms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    /**
     * Generate Time Tracking Report for an employee
     * Shows all day logs, work times, OT breakdown
     */
    @GetMapping("/time-tracking")
    public ResponseEntity<TimeTrackingReportResponse> getTimeTrackingReport(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        TimeTrackingReportResponse report = reportService.generateTimeTrackingReport(
            employeeId, startDate, endDate
        );
        return ResponseEntity.ok(report);
    }

    /**
     * Generate OT Tracking Report for an employee
     * Shows detailed OT breakdown, generator work categorization, and performance analysis
     */
    @GetMapping("/ot-tracking")
    public ResponseEntity<OTTrackingReportResponse> getOTTrackingReport(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        OTTrackingReportResponse report = reportService.generateOTTrackingReport(
            employeeId, startDate, endDate
        );
        return ResponseEntity.ok(report);
    }
}
