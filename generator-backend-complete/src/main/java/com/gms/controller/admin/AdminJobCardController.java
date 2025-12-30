package com.gms.controller.admin;

import com.gms.dto.request.JobCardApprovalRequest;
import com.gms.dto.response.JobCardResponse;
import com.gms.entity.MiniJobCard;
import com.gms.enums.JobStatus;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.MiniJobCardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.gms.entity.Generator;
import com.gms.dto.response.GeneratorResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/job-cards")
@RequiredArgsConstructor
public class AdminJobCardController {

    private final MiniJobCardRepository jobCardRepository;

    /**
     * Get all job cards with optional status filter
     */
    @GetMapping
    public ResponseEntity<Page<JobCardResponse>> getAllJobCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) JobStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<MiniJobCard> jobCards;

        if (status != null) {
            jobCards = jobCardRepository.findByStatus(status, pageable);
        } else {
            jobCards = jobCardRepository.findAll(pageable);
        }

        Page<JobCardResponse> response = jobCards.map(this::mapToResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * Get job card by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobCardResponse> getJobCardById(@PathVariable Long id) {
        MiniJobCard jobCard = jobCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job card not found"));
        return ResponseEntity.ok(mapToResponse(jobCard));
    }

    /**
     * Get all job cards pending approval (COMPLETED status)
     */
    @GetMapping("/pending-approval")
    public ResponseEntity<List<JobCardResponse>> getPendingApprovals() {
        List<MiniJobCard> pendingJobCards = jobCardRepository.findByStatusAndApproved(JobStatus.COMPLETED, false);

        List<JobCardResponse> response = pendingJobCards.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Approve or reject a job card
     */
    @PostMapping("/{id}/approve")
    @Transactional
    public ResponseEntity<JobCardResponse> approveJobCard(
            @PathVariable Long id,
            @Valid @RequestBody JobCardApprovalRequest request) {

        MiniJobCard jobCard = jobCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job card not found"));

        // Verify job card is completed
        if (jobCard.getStatus() != JobStatus.COMPLETED) {
            throw new IllegalArgumentException("Job card must be completed before approval");
        }

        // Set approval status
        jobCard.setApproved(request.getApproved());

        // Save the job card
        MiniJobCard savedJobCard = jobCardRepository.save(jobCard);

        return ResponseEntity.ok(mapToResponse(savedJobCard));
    }

    /**
     * Get job cards by main ticket ID
     */
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<JobCardResponse>> getJobCardsByTicket(@PathVariable Long ticketId) {
        List<MiniJobCard> jobCards = jobCardRepository.findByMainTicketId(ticketId);

        List<JobCardResponse> response = jobCards.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
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
