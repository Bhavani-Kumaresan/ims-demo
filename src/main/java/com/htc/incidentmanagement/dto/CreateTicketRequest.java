package com.htc.incidentmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for creating new incident ticket")
public class CreateTicketRequest {

    @Schema(description = "Ticket title", example = "VPN Connection Timeout", required = true)
    private String title;

    @Schema(description = "Ticket priority", example = "HIGH", required = true)
    private String priority;

    @Schema(description = "Initial ticket status", example = "OPEN")
    private String status = "OPEN";

    // Getters/Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
