package com.htc.incidentmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating ticket assignment")
public class CreateAssignmentRequest {

    @NotNull(message = "ticketId is required")
    @Schema(description = "ID of the ticket being assigned", example = "10", required = true)
    private Long ticketId;

    @NotNull(message = "employeeId is required")
    @Schema(description = "ID of the employee/agent receiving the assignment", example = "2", required = true)
    private Long employeeId;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    @Schema(description = "Optional comment explaining the assignment reason", example = "Assigning to network team")
    private String comment;

    public CreateAssignmentRequest() {
    }

    public CreateAssignmentRequest(Long ticketId, Long employeeId, String comment) {
        this.ticketId = ticketId;
        this.employeeId = employeeId;
        this.comment = comment;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
