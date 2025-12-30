package com.gms.enums;

public enum TicketStatus {
    CREATED,        // Main ticket created, awaiting assignment
    ASSIGNED,       // Sub-tickets assigned to employees
    IN_PROGRESS,    // Employee started working on the ticket
    COMPLETED,      // Employee completed their sub-ticket
    CLOSED          // Main ticket closed (all sub-tickets completed)
}
