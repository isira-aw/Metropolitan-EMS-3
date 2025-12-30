package com.gms.controller.admin;

import com.gms.dto.request.ApprovalRequest;
import com.gms.dto.request.MainTicketRequest;
import com.gms.dto.response.MainTicketResponse;
import com.gms.dto.response.SubTicketResponse;
import com.gms.entity.Generator;
import com.gms.entity.MainTicket;
import com.gms.entity.SubTicket;
import com.gms.entity.User;
import com.gms.enums.Role;
import com.gms.enums.TicketStatus;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.GeneratorRepository;
import com.gms.repository.MainTicketRepository;
import com.gms.repository.SubTicketRepository;
import com.gms.repository.UserRepository;
import com.gms.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {

    private final MainTicketRepository ticketRepository;
    private final SubTicketRepository subTicketRepository;
    private final GeneratorRepository generatorRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<MainTicketResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) LocalDate scheduledDate
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<MainTicket> tickets;

        if (status != null) {
            tickets = ticketRepository.findByStatus(status, pageable);
        } else if (scheduledDate != null) {
            tickets = ticketRepository.findByScheduledDate(scheduledDate, pageable);
        } else {
            tickets = ticketRepository.findAllWithDetails(pageable);
        }

        Page<MainTicketResponse> response = tickets.map(this::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MainTicketResponse> getTicketById(@PathVariable Long id) {
        MainTicket ticket = ticketRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return ResponseEntity.ok(toResponse(ticket));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<MainTicketResponse> createTicket(@Valid @RequestBody MainTicketRequest request) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get generator
        Generator generator = generatorRepository.findById(request.getGeneratorId())
            .orElseThrow(() -> new ResourceNotFoundException("Generator not found"));

        // Validate employees exist and are EMPLOYEE role
        List<User> employees = new ArrayList<>();
        for (Long employeeId : request.getEmployeeIds()) {
            User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

            if (employee.getRole() != Role.EMPLOYEE) {
                throw new IllegalArgumentException("User with ID " + employeeId + " is not an employee");
            }
            employees.add(employee);
        }

        // Auto-generate ticket number
        String ticketNumber = generateTicketNumber();

        // Create main ticket
        MainTicket ticket = MainTicket.builder()
            .ticketNumber(ticketNumber)
            .generator(generator)
            .title(request.getTitle())
            .description(request.getDescription())
            .weight(request.getWeight())
            .status(TicketStatus.ASSIGNED)  // Set to ASSIGNED since we're assigning employees
            .scheduledDate(request.getScheduledDate())
            .scheduledTime(request.getScheduledTime())
            .createdBy(currentUser)
            .build();

        MainTicket savedTicket = ticketRepository.save(ticket);

        // Create sub-tickets for each assigned employee
        // Get the next available sub-ticket sequence number to prevent duplicates
        Integer maxSequence = subTicketRepository.findMaxSubTicketSequenceForMainTicket(ticketNumber);
        int subTicketCounter = (maxSequence != null ? maxSequence : 0) + 1;

        for (User employee : employees) {
            String subTicketNumber = ticketNumber + "-" + String.format("%02d", subTicketCounter++);

            SubTicket subTicket = SubTicket.builder()
                .ticketNumber(subTicketNumber)
                .mainTicket(savedTicket)
                .employee(employee)
                .status(TicketStatus.ASSIGNED)
                .build();

            subTicketRepository.save(subTicket);
        }

        return ResponseEntity.ok(toResponse(savedTicket));
    }

    /**
     * Generate unique ticket number in format: TKT-YYYYMMDD-XXXX
     */
    private String generateTicketNumber() {
        String datePrefix = "TKT-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";

        Integer maxNumber = ticketRepository.findMaxTicketNumberForPrefix(datePrefix);
        int nextNumber = (maxNumber != null ? maxNumber : 0) + 1;

        return datePrefix + String.format("%04d", nextNumber);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<MainTicketResponse> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody MainTicketRequest request) {
        MainTicket ticket = ticketRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Get generator
        Generator generator = generatorRepository.findById(request.getGeneratorId())
            .orElseThrow(() -> new ResourceNotFoundException("Generator not found"));

        // Update basic fields
        ticket.setGenerator(generator);
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setWeight(request.getWeight());
        ticket.setScheduledDate(request.getScheduledDate());
        ticket.setScheduledTime(request.getScheduledTime());

        // Handle employee reassignments if provided
        if (request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()) {
            // Validate employees
            List<User> employees = new ArrayList<>();
            for (Long employeeId : request.getEmployeeIds()) {
                User employee = userRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

                if (employee.getRole() != Role.EMPLOYEE) {
                    throw new IllegalArgumentException("User with ID " + employeeId + " is not an employee");
                }
                employees.add(employee);
            }

            // Delete existing sub-tickets
            List<SubTicket> existingSubTickets = subTicketRepository.findByMainTicketId(id);
            subTicketRepository.deleteAll(existingSubTickets);

            // Create new sub-tickets
            // Get the next available sub-ticket sequence number to prevent duplicates
            Integer maxSequence = subTicketRepository.findMaxSubTicketSequenceForMainTicket(ticket.getTicketNumber());
            int subTicketCounter = (maxSequence != null ? maxSequence : 0) + 1;

            for (User employee : employees) {
                String subTicketNumber = ticket.getTicketNumber() + "-" + String.format("%02d", subTicketCounter++);

                SubTicket subTicket = SubTicket.builder()
                    .ticketNumber(subTicketNumber)
                    .mainTicket(ticket)
                    .employee(employee)
                    .status(TicketStatus.ASSIGNED)
                    .build();

                subTicketRepository.save(subTicket);
            }

            ticket.setStatus(TicketStatus.ASSIGNED);
        }

        MainTicket updatedTicket = ticketRepository.save(ticket);
        return ResponseEntity.ok(toResponse(updatedTicket));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        MainTicket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Delete associated sub-tickets first
        List<SubTicket> subTickets = subTicketRepository.findByMainTicketId(id);
        subTicketRepository.deleteAll(subTickets);

        // Delete main ticket
        ticketRepository.delete(ticket);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MainTicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {
        MainTicket ticket = ticketRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticket.setStatus(status);
        MainTicket updatedTicket = ticketRepository.save(ticket);

        return ResponseEntity.ok(toResponse(updatedTicket));
    }

    private MainTicketResponse toResponse(MainTicket ticket) {
        // Get sub-tickets for this main ticket
        List<SubTicket> subTickets = subTicketRepository.findByMainTicketId(ticket.getId());

        // Convert to sub-ticket responses
        List<SubTicketResponse> subTicketResponses = subTickets.stream()
            .map(this::toSubTicketResponse)
            .collect(Collectors.toList());

        // Count completed sub-tickets
        long completedCount = subTickets.stream()
            .filter(st -> st.getStatus() == TicketStatus.COMPLETED || st.getStatus() == TicketStatus.CLOSED)
            .count();

        return MainTicketResponse.builder()
            .id(ticket.getId())
            .ticketNumber(ticket.getTicketNumber())
            .generatorId(ticket.getGenerator().getId())
            .generatorName(ticket.getGenerator().getName())
            .generatorModel(ticket.getGenerator().getModel())
            .title(ticket.getTitle())
            .description(ticket.getDescription())
            .weight(ticket.getWeight())
            .weightDisplay(convertWeightToStars(ticket.getWeight()))
            .status(ticket.getStatus())
            .scheduledDate(ticket.getScheduledDate())
            .scheduledTime(ticket.getScheduledTime())
            .createdById(ticket.getCreatedBy().getId())
            .createdByName(ticket.getCreatedBy().getFullName())
            .createdAt(ticket.getCreatedAt())
            .subTickets(subTicketResponses)
            .totalAssignments(subTickets.size())
            .completedAssignments((int) completedCount)
            .build();
    }

    private String convertWeightToStars(Integer weight) {
        if (weight == null || weight < 1 || weight > 5) {
            return "";
        }
        return "*".repeat(weight);
    }

    private SubTicketResponse toSubTicketResponse(SubTicket subTicket) {
        SubTicketResponse.SubTicketResponseBuilder builder = SubTicketResponse.builder()
            .id(subTicket.getId())
            .ticketNumber(subTicket.getTicketNumber())
            .mainTicketId(subTicket.getMainTicket().getId())
            .mainTicketNumber(subTicket.getMainTicket().getTicketNumber())
            // Main ticket details
            .mainTicketTitle(subTicket.getMainTicket().getTitle())
            .mainTicketDescription(subTicket.getMainTicket().getDescription())
            .scheduledDate(subTicket.getMainTicket().getScheduledDate())
            .scheduledTime(subTicket.getMainTicket().getScheduledTime())
            .mainTicketWeight(subTicket.getMainTicket().getWeight())
            // Generator information
            .generatorId(subTicket.getMainTicket().getGenerator().getId())
            .generatorName(subTicket.getMainTicket().getGenerator().getName())
            .generatorModel(subTicket.getMainTicket().getGenerator().getModel())
            .generatorLocation(subTicket.getMainTicket().getGenerator().getLocationName())
            // Employee information
            .employeeId(subTicket.getEmployee().getId())
            .employeeName(subTicket.getEmployee().getFullName())
            .employeeEmail(subTicket.getEmployee().getEmail())
            .status(subTicket.getStatus())
            .notes(subTicket.getNotes())
            .completionFactor(subTicket.getCompletionFactor())
            .qualityFactor(subTicket.getQualityFactor())
            .score(subTicket.getScore())
            .approved(subTicket.getApproved())
            .adminReviewNotes(subTicket.getAdminReviewNotes())
            .createdAt(subTicket.getCreatedAt())
            .updatedAt(subTicket.getUpdatedAt())
            .completedAt(subTicket.getCompletedAt());

        if (subTicket.getApprovedBy() != null) {
            builder.approvedById(subTicket.getApprovedBy().getId())
                   .approvedByName(subTicket.getApprovedBy().getFullName());
        }

        builder.approvedAt(subTicket.getApprovedAt());

        return builder.build();
    }

    /**
     * Approve or reject a sub-ticket and calculate score
     */
    @PostMapping("/sub-tickets/{subTicketId}/approve")
    @Transactional
    public ResponseEntity<SubTicketResponse> approveSubTicket(
            @PathVariable Long subTicketId,
            @Valid @RequestBody ApprovalRequest request) {

        SubTicket subTicket = subTicketRepository.findByIdWithDetails(subTicketId)
            .orElseThrow(() -> new ResourceNotFoundException("Sub-ticket not found"));

        // Verify sub-ticket is completed
        if (subTicket.getStatus() != TicketStatus.COMPLETED && subTicket.getStatus() != TicketStatus.PENDING_APPROVAL) {
            throw new IllegalArgumentException("Sub-ticket must be completed before approval");
        }

        // Get current admin user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User admin = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        if (request.getApproved()) {
            // Approve the work
            subTicket.setStatus(TicketStatus.APPROVED);
            subTicket.setApproved(true);

            // Set scoring factors (default to 1.0 if not provided)
            double completionFactor = request.getCompletionFactor() != null ? request.getCompletionFactor() : 1.0;
            double qualityFactor = request.getQualityFactor() != null ? request.getQualityFactor() : 1.0;

            subTicket.setCompletionFactor(completionFactor);
            subTicket.setQualityFactor(qualityFactor);

            // Calculate score: weight × completionFactor × qualityFactor
            Integer weight = subTicket.getMainTicket().getWeight();
            double score = weight * completionFactor * qualityFactor;
            subTicket.setScore(score);

        } else {
            // Reject the work
            subTicket.setStatus(TicketStatus.REJECTED);
            subTicket.setApproved(false);
        }

        subTicket.setApprovedBy(admin);
        subTicket.setApprovedAt(LocalDateTime.now());
        subTicket.setAdminReviewNotes(request.getAdminReviewNotes());

        SubTicket savedSubTicket = subTicketRepository.save(subTicket);

        return ResponseEntity.ok(toSubTicketResponse(savedSubTicket));
    }

    /**
     * Get all sub-tickets pending approval
     */
    @GetMapping("/sub-tickets/pending-approval")
    public ResponseEntity<List<SubTicketResponse>> getPendingApprovals() {
        List<SubTicket> pendingTickets = subTicketRepository.findByStatus(TicketStatus.COMPLETED);

        List<SubTicketResponse> response = pendingTickets.stream()
            .map(this::toSubTicketResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
