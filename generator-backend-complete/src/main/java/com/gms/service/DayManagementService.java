package com.gms.service;

import com.gms.dto.response.DayStatusResponse;
import com.gms.entity.EmployeeDayLog;
import com.gms.entity.User;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.EmployeeDayLogRepository;
import com.gms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DayManagementService {
    
    private final EmployeeDayLogRepository dayLogRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public DayStatusResponse startDay(Long employeeId) {
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        LocalDate today = LocalDate.now();
        
        EmployeeDayLog dayLog = dayLogRepository
            .findByEmployeeIdAndDayDate(employeeId, today)
            .orElse(EmployeeDayLog.builder()
                .employee(employee)
                .dayDate(today)
                .build());
        
        if (dayLog.getDayStartTime() != null) {
            throw new IllegalStateException("Day already started");
        }
        
        LocalDateTime now = LocalDateTime.now();
        dayLog.setDayStartTime(now);
        
        // Calculate morning OT
        LocalTime workStart = LocalTime.of(8, 30);
        if (now.toLocalTime().isBefore(workStart)) {
            long morningMinutes = java.time.Duration.between(now.toLocalTime(), workStart).toMinutes();
            dayLog.setMorningOtMinutes((int) morningMinutes);
        }
        
        dayLogRepository.save(dayLog);
        
        return DayStatusResponse.builder()
            .dayStarted(true)
            .dayEnded(false)
            .dayStartTime(now)
            .build();
    }
    
    @Transactional
    public DayStatusResponse endDay(Long employeeId) {
        LocalDate today = LocalDate.now();
        
        EmployeeDayLog dayLog = dayLogRepository
            .findByEmployeeIdAndDayDate(employeeId, today)
            .orElseThrow(() -> new ResourceNotFoundException("Day not started"));
        
        if (dayLog.getDayEndTime() != null) {
            throw new IllegalStateException("Day already ended");
        }
        
        LocalDateTime now = LocalDateTime.now();
        dayLog.setDayEndTime(now);
        
        // Calculate evening OT
        LocalTime workEnd = LocalTime.of(17, 30);
        if (now.toLocalTime().isAfter(workEnd)) {
            long eveningMinutes = java.time.Duration.between(workEnd, now.toLocalTime()).toMinutes();
            dayLog.setEveningOtMinutes((int) eveningMinutes);
        }
        
        // Calculate total work minutes
        long totalMinutes = java.time.Duration.between(dayLog.getDayStartTime(), now).toMinutes();
        dayLog.setTotalWorkMinutes((int) totalMinutes);
        
        dayLogRepository.save(dayLog);
        
        return DayStatusResponse.builder()
            .dayStarted(true)
            .dayEnded(true)
            .dayStartTime(dayLog.getDayStartTime())
            .dayEndTime(now)
            .build();
    }
    
    public DayStatusResponse getDayStatus(Long employeeId) {
        LocalDate today = LocalDate.now();
        
        return dayLogRepository.findByEmployeeIdAndDayDate(employeeId, today)
            .map(log -> DayStatusResponse.builder()
                .dayStarted(log.getDayStartTime() != null)
                .dayEnded(log.getDayEndTime() != null)
                .dayStartTime(log.getDayStartTime())
                .dayEndTime(log.getDayEndTime())
                .build())
            .orElse(DayStatusResponse.builder()
                .dayStarted(false)
                .dayEnded(false)
                .build());
    }
}
