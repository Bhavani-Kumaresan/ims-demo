package com.htc.incidentmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Minimal ticket info for category listing")
public class TicketSummary {

    @Schema(example = "2")
    private Long ticketId;

    @Schema(example = "VPN Connection Timeout")
    private String title;

    @Schema(example = "HIGH")
    private String priority;

    @Schema(example = "IN_PROGRESS")
    private String status;

    public TicketSummary() {
    }

    public TicketSummary(Long ticketId, String title, String priority, String status) {
        this.ticketId = ticketId;
        this.title = title;
        this.priority = priority;
        this.status = status;
    }

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
}
