package com.htc.incidentmanagement.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ticket assignment response.")
public class TicketAssignmentResponse {

    @Schema(description = "Assignment ID", example = "1")
    private Long assignmentId;

    @Schema(description = "Ticket ID")
    private Long ticketId;

    @Schema(description = "Ticket title")
    private String ticketTitle;

    @Schema(description = "Employee ID assigned")
    private Long employeeId;

    @Schema(description = "Employee name")
    private String employeeName;

    @Schema(description = "Optional comment for the assignment")
    private String comment;

    @Schema(description = "Assignment timestamp")
    private LocalDateTime assignedAt;

    // Getters and setters
    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketTitle() {
        return ticketTitle;
    }

    public void setTicketTitle(String ticketTitle) {
        this.ticketTitle = ticketTitle;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
