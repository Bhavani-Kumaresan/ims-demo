package com.htc.incidentmanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.htc.incidentmanagement.model.IncidentTicket;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Incident ticket response.")
public class TicketResponse {

    @Schema(example = "10")
    private Long ticketId;

    @Schema(example = "VPN Connection Timeout")
    private String title;

    @Schema(example = "HIGH")
    private String priority;

    @Schema(example = "IN_PROGRESS")
    private String status;

    private Boolean approved;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

    // Summaries (no back-references)
    private EmployeeSummary createdBy;
    private EmployeeSummary assignedTo;
    private EmployeeSummary closedBy;

    private CategorySummary category;
    private SLASummary sla;

    // ------------------------------
    // Assignment audit history
    // ------------------------------
    private List<TicketAssignmentAuditRecord> assignmentHistory;

    public TicketResponse() {
    }

    // Constructor from entity
    public TicketResponse(IncidentTicket ticket, List<TicketAssignmentAuditRecord> assignmentHistory) {
        this.ticketId = ticket.getTicketId();
        this.title = ticket.getTitle();
        this.priority = ticket.getPriority();
        this.status = ticket.getStatus();
        this.approved = ticket.getApproved();
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
        this.closedAt = ticket.getClosedAt();

        if (ticket.getCreatedBy() != null) {
            this.createdBy = new EmployeeSummary(ticket.getCreatedBy());
        }
        if (ticket.getAssignedTo() != null) {
            this.assignedTo = new EmployeeSummary(ticket.getAssignedTo());
        }
        if (ticket.getClosedBy() != null) {
            this.closedBy = new EmployeeSummary(ticket.getClosedBy());
        }
        if (ticket.getCategory() != null) {
            this.category = new CategorySummary(ticket.getCategory());
        }
        if (ticket.getSla() != null) {
            this.sla = new SLASummary(ticket.getSla());
        }

        // Assignment audit history
        this.assignmentHistory = assignmentHistory;
    }

    // ---------- Setters ----------
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public void setCreatedBy(EmployeeSummary createdBy) {
        this.createdBy = createdBy;
    }

    public void setAssignedTo(EmployeeSummary assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setClosedBy(EmployeeSummary closedBy) {
        this.closedBy = closedBy;
    }

    public void setCategory(CategorySummary category) {
        this.category = category;
    }

    public void setSla(SLASummary sla) {
        this.sla = sla;
    }

    public void setAssignmentHistory(List<TicketAssignmentAuditRecord> assignmentHistory) {
        this.assignmentHistory = assignmentHistory;
    }

    // ---------- Getters ----------
    public Long getTicketId() {
        return ticketId;
    }

    public String getTitle() {
        return title;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getApproved() {
        return approved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public EmployeeSummary getCreatedBy() {
        return createdBy;
    }

    public EmployeeSummary getAssignedTo() {
        return assignedTo;
    }

    public EmployeeSummary getClosedBy() {
        return closedBy;
    }

    public CategorySummary getCategory() {
        return category;
    }

    public SLASummary getSla() {
        return sla;
    }

    public List<TicketAssignmentAuditRecord> getAssignmentHistory() {
        return assignmentHistory;
    }

}
