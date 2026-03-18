package com.htc.incidentmanagement.dto;

import com.htc.incidentmanagement.model.SLA;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary SLA information ")
public class SLASummary {

    @Schema(description = "Unique SLA identifier", example = "1")
    private Long slaId;

    @Schema(description = "Priority level (HIGH, MEDIUM, LOW)", example = "HIGH")
    private String priority;

    @Schema(description = "Required resolution time in hours", example = "4")
    private int resolutionTimeHours;

    // Constructor from SLA entity
    public SLASummary(SLA sla) {
        this.slaId = sla.getSlaId();
        this.priority = sla.getPriority();
        this.resolutionTimeHours = sla.getResolutionTimeHours();
    }

    public SLASummary() {
    }

    // Getters
    public Long getSlaId() {
        return slaId;
    }

    public String getPriority() {
        return priority;
    }

    public int getResolutionTimeHours() {
        return resolutionTimeHours;
    }

    // Setters (optional - for JSON deserialization)
    public void setSlaId(Long slaId) {
        this.slaId = slaId;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setResolutionTimeHours(int resolutionTimeHours) {
        this.resolutionTimeHours = resolutionTimeHours;
    }
}
