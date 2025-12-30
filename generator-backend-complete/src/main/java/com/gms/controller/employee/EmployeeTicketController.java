package com.gms.controller.employee;

import com.gms.dto.response.SubTicketResponse;
import com.gms.entity.SubTicket;
import com.gms.enums.TicketStatus;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.SubTicketRepository;
import com.gms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee/tickets")
@RequiredArgsConstructor
public class EmployeeTicketController {

    private final SubTicketRepository subTicketRepository;

    /**
     * Get all tickets assigned to the current employee
     */
    @GetMapping
    public ResponseEntity<List<SubTicketResponse>> getMyTickets() {
        Long employeeId = getCurrentUserId();
        List<SubTicket> subTickets = subTicketRepository.findByEmployeeId(employeeId);

        List<SubTicketResponse> response = subTickets.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific sub-ticket by ID (only if assigned to current employee)
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubTicketResponse> getTicketById(@PathVariable Long id) {
        Long employeeId = getCurrentUserId();
        SubTicket subTicket = subTicketRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Verify the ticket is assigned to the current employee
        if (!subTicket.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("You are not authorized to view this ticket");
        }

        return ResponseEntity.ok(toResponse(subTicket));
    }

    /**
     * Update sub-ticket status (employee can mark as IN_PROGRESS or COMPLETED)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<SubTicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {

        // Validate status - employees can only set IN_PROGRESS or COMPLETED
        if (status != TicketStatus.IN_PROGRESS && status != TicketStatus.COMPLETED) {
            throw new IllegalArgumentException("Employees can only set status to IN_PROGRESS or COMPLETED");
        }

        Long employeeId = getCurrentUserId();
        SubTicket subTicket = subTicketRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Verify the ticket is assigned to the current employee
        if (!subTicket.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("You are not authorized to update this ticket");
        }

        subTicket.setStatus(status);
        SubTicket updatedTicket = subTicketRepository.save(subTicket);

        return ResponseEntity.ok(toResponse(updatedTicket));
    }

    /**
     * Add or update notes for a sub-ticket
     */
    @PatchMapping("/{id}/notes")
    public ResponseEntity<SubTicketResponse> updateNotes(
            @PathVariable Long id,
            @RequestBody String notes) {

        Long employeeId = getCurrentUserId();
        SubTicket subTicket = subTicketRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Verify the ticket is assigned to the current employee
        if (!subTicket.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("You are not authorized to update this ticket");
        }

        subTicket.setNotes(notes);
        SubTicket updatedTicket = subTicketRepository.save(subTicket);

        return ResponseEntity.ok(toResponse(updatedTicket));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }

    private SubTicketResponse toResponse(SubTicket subTicket) {
        return SubTicketResponse.builder()
            .id(subTicket.getId())
            .ticketNumber(subTicket.getTicketNumber())
            .mainTicketId(subTicket.getMainTicket().getId())
            .mainTicketNumber(subTicket.getMainTicket().getTicketNumber())
            .employeeId(subTicket.getEmployee().getId())
            .employeeName(subTicket.getEmployee().getFullName())
            .employeeEmail(subTicket.getEmployee().getEmail())
            .status(subTicket.getStatus())
            .notes(subTicket.getNotes())
            .createdAt(subTicket.getCreatedAt())
            .updatedAt(subTicket.getUpdatedAt())
            .build();
    }
}
