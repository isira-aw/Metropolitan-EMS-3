package com.gms.controller.admin;

import com.gms.dto.request.MainTicketRequest;
import com.gms.dto.response.MainTicketResponse;
import com.gms.entity.Generator;
import com.gms.entity.MainTicket;
import com.gms.entity.User;
import com.gms.enums.TicketStatus;
import com.gms.exception.ResourceNotFoundException;
import com.gms.repository.GeneratorRepository;
import com.gms.repository.MainTicketRepository;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {

    private final MainTicketRepository ticketRepository;
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
            tickets = ticketRepository.findAll(pageable);
        }

        Page<MainTicketResponse> response = tickets.map(this::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MainTicketResponse> getTicketById(@PathVariable Long id) {
        MainTicket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return ResponseEntity.ok(toResponse(ticket));
    }

    @PostMapping
    public ResponseEntity<MainTicketResponse> createTicket(@Valid @RequestBody MainTicketRequest request) {
        // Check if ticket number already exists
        if (ticketRepository.existsByTicketNumber(request.getTicketNumber())) {
            throw new IllegalArgumentException("Ticket number already exists");
        }

        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get generator
        Generator generator = generatorRepository.findById(request.getGeneratorId())
            .orElseThrow(() -> new ResourceNotFoundException("Generator not found"));

        MainTicket ticket = MainTicket.builder()
            .ticketNumber(request.getTicketNumber())
            .generator(generator)
            .title(request.getTitle())
            .description(request.getDescription())
            .weight(request.getWeight())
            .status(request.getStatus() != null ? request.getStatus() : TicketStatus.CREATED)
            .scheduledDate(request.getScheduledDate())
            .scheduledTime(request.getScheduledTime())
            .createdBy(currentUser)
            .build();

        MainTicket savedTicket = ticketRepository.save(ticket);
        return ResponseEntity.ok(toResponse(savedTicket));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MainTicketResponse> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody MainTicketRequest request) {
        MainTicket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Check if ticket number is being changed and if it already exists
        if (!ticket.getTicketNumber().equals(request.getTicketNumber()) &&
            ticketRepository.existsByTicketNumber(request.getTicketNumber())) {
            throw new IllegalArgumentException("Ticket number already exists");
        }

        // Get generator
        Generator generator = generatorRepository.findById(request.getGeneratorId())
            .orElseThrow(() -> new ResourceNotFoundException("Generator not found"));

        ticket.setTicketNumber(request.getTicketNumber());
        ticket.setGenerator(generator);
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setWeight(request.getWeight());
        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
        }
        ticket.setScheduledDate(request.getScheduledDate());
        ticket.setScheduledTime(request.getScheduledTime());

        MainTicket updatedTicket = ticketRepository.save(ticket);
        return ResponseEntity.ok(toResponse(updatedTicket));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        MainTicket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticketRepository.delete(ticket);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MainTicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {
        MainTicket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticket.setStatus(status);
        MainTicket updatedTicket = ticketRepository.save(ticket);

        return ResponseEntity.ok(toResponse(updatedTicket));
    }

    private MainTicketResponse toResponse(MainTicket ticket) {
        return MainTicketResponse.builder()
            .id(ticket.getId())
            .ticketNumber(ticket.getTicketNumber())
            .generatorId(ticket.getGenerator().getId())
            .generatorName(ticket.getGenerator().getName())
            .generatorModel(ticket.getGenerator().getModel())
            .title(ticket.getTitle())
            .description(ticket.getDescription())
            .weight(ticket.getWeight())
            .status(ticket.getStatus())
            .scheduledDate(ticket.getScheduledDate())
            .scheduledTime(ticket.getScheduledTime())
            .createdById(ticket.getCreatedBy().getId())
            .createdByName(ticket.getCreatedBy().getFullName())
            .createdAt(ticket.getCreatedAt())
            .build();
    }
}
