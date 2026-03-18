package com.htc.incidentmanagement.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ticket assignment audit info")
public record TicketAssignmentAuditRecord(
                Long auditId,
                EmployeeSummary assignedTo,
                EmployeeSummary assignedBy,
                LocalDateTime assignedAt,
                LocalDateTime updatedAt,
                LocalDateTime closedAt) {
}
