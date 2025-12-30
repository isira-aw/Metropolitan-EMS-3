package com.gms.service;

import com.gms.dto.request.StatusUpdateRequest;
import com.gms.dto.response.GeneratorResponse;
import com.gms.dto.response.JobCardResponse;
import com.gms.entity.*;
import com.gms.enums.JobStatus;
import com.gms.exception.DayNotStartedException;
import com.gms.exception.InvalidStatusTransitionException;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class JobCardService {
    
    private final MiniJobCardRepository jobCardRepository;
    private final JobStatusLogRepository statusLogRepository;
    private final EmployeeDayLogRepository dayLogRepository;
    private final UserRepository userRepository;
    
    public Page<JobCardResponse> getEmployeeJobCards(Long employeeId, JobStatus status, Pageable pageable) {
        Page<MiniJobCard> jobCards;
        
        if (status != null) {
            jobCards = jobCardRepository.findByEmployeeIdAndStatus(employeeId, status, pageable);
        } else {
            jobCards = jobCardRepository.findByEmployeeId(employeeId, pageable);
        }
        
        return jobCards.map(this::mapToResponse);
    }
    
    @Transactional
    public JobCardResponse updateStatus(Long jobCardId, Long employeeId, StatusUpdateRequest request) {
        // Check day started
        LocalDate today = LocalDate.now();
        EmployeeDayLog dayLog = dayLogRepository.findByEmployeeIdAndDayDate(employeeId, today)
            .orElseThrow(() -> new DayNotStartedException("Please start your day first"));
        
        if (dayLog.getDayStartTime() == null) {
            throw new DayNotStartedException("Please start your day first");
        }
        
        if (dayLog.getDayEndTime() != null) {
            throw new IllegalStateException("Day already ended. Cannot update status");
        }
        
        // Get job card
        MiniJobCard jobCard = jobCardRepository.findById(jobCardId)
            .orElseThrow(() -> new ResourceNotFoundException("Job card not found"));
        
        // Validate transition
        if (!jobCard.getStatus().canTransitionTo(request.getNewStatus())) {
            throw new InvalidStatusTransitionException(
                String.format("Cannot transition from %s to %s", jobCard.getStatus(), request.getNewStatus())
            );
        }
        
        // Create status log
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        JobStatusLog log = JobStatusLog.builder()
            .miniJobCard(jobCard)
            .generator(jobCard.getMainTicket().getGenerator().getName())
            .employeeEmail(employee.getEmail())
            .passStatus(jobCard.getStatus())
            .newStatus(request.getNewStatus())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .updatedBy(employee)
            .build();
        
        statusLogRepository.save(log);
        
        // Update job card
        jobCard.setStatus(request.getNewStatus());
        
        if (request.getNewStatus() == JobStatus.STARTED && jobCard.getStartTime() == null) {
            jobCard.setStartTime(java.time.LocalDateTime.now());
        }
        
        if (request.getNewStatus() == JobStatus.COMPLETED && jobCard.getEndTime() == null) {
            jobCard.setEndTime(java.time.LocalDateTime.now());
            // Calculate work minutes
            if (jobCard.getStartTime() != null) {
                long minutes = java.time.Duration.between(jobCard.getStartTime(), jobCard.getEndTime()).toMinutes();
                jobCard.setWorkMinutes((int) minutes);
            }
        }
        
        jobCardRepository.save(jobCard);
        
        return mapToResponse(jobCard);
    }
    
    private JobCardResponse mapToResponse(MiniJobCard jobCard) {
        Generator gen = jobCard.getMainTicket().getGenerator();
        
        return JobCardResponse.builder()
            .id(jobCard.getId())
            .mainTicketId(jobCard.getMainTicket().getId())
            .ticketNumber(jobCard.getMainTicket().getTicketNumber())
            .ticketTitle(jobCard.getMainTicket().getTitle())
            .generator(GeneratorResponse.builder()
                .id(gen.getId())
                .model(gen.getModel())
                .name(gen.getName())
                .capacity(gen.getCapacity())
                .locationName(gen.getLocationName())
                .ownerEmail(gen.getOwnerEmail())
                .latitude(gen.getLatitude())
                .longitude(gen.getLongitude())
                .note(gen.getNote())
                .createdAt(gen.getCreatedAt())
                .build())
            .status(jobCard.getStatus())
            .startTime(jobCard.getStartTime())
            .endTime(jobCard.getEndTime())
            .workMinutes(jobCard.getWorkMinutes())
            .approved(jobCard.getApproved())
            .image(jobCard.getImage())
            .createdAt(jobCard.getCreatedAt())
            .build();
    }
}
