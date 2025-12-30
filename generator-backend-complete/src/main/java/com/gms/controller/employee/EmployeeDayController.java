package com.gms.controller.employee;

import com.gms.dto.response.DayStatusResponse;
import com.gms.security.UserPrincipal;
import com.gms.service.DayManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee/day")
@RequiredArgsConstructor
public class EmployeeDayController {
    
    private final DayManagementService dayManagementService;
    
    @PostMapping("/start")
    public ResponseEntity<DayStatusResponse> startDay(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(dayManagementService.startDay(userPrincipal.getId()));
    }
    
    @PostMapping("/end")
    public ResponseEntity<DayStatusResponse> endDay(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(dayManagementService.endDay(userPrincipal.getId()));
    }
    
    @GetMapping("/status")
    public ResponseEntity<DayStatusResponse> getDayStatus(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(dayManagementService.getDayStatus(userPrincipal.getId()));
    }
}
