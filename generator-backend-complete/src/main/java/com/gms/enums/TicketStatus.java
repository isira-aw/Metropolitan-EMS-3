package com.gms.enums;

public enum TicketStatus {
    CREATED,            // Main ticket created, awaiting assignment
    ASSIGNED,           // Sub-tickets assigned to employees
    IN_PROGRESS,        // Employee started working on the ticket
    COMPLETED,          // Employee completed their sub-ticket, awaiting admin review
    PENDING_APPROVAL,   // Waiting for admin approval (same as COMPLETED)
    APPROVED,           // Admin approved the work, score locked
    REJECTED,           // Admin rejected the work, needs rework
    CLOSED              // Main ticket closed (all sub-tickets completed and approved)
}
