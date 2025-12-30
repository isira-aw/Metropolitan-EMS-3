package com.gms.controller.employee;

import com.gms.dto.request.StatusUpdateRequest;
import com.gms.dto.response.JobCardResponse;
import com.gms.enums.JobStatus;
import com.gms.security.UserPrincipal;
import com.gms.service.JobCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee/job-cards")
@RequiredArgsConstructor
public class EmployeeJobCardController {
    
    private final JobCardService jobCardService;
    
    @GetMapping
    public ResponseEntity<Page<JobCardResponse>> getMyJobCards(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) JobStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(
            jobCardService.getEmployeeJobCards(userPrincipal.getId(), status, pageable)
        );
    }
    
    @PostMapping("/{id}/status")
    public ResponseEntity<JobCardResponse> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
            jobCardService.updateStatus(id, userPrincipal.getId(), request)
        );
    }
}
