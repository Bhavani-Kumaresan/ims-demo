package com.htc.incidentmanagement.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ims_sla")
@Schema(description = "Service Level Agreement by priority")
public class SLA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sla_id")
    private Long slaId;

    @Column(nullable = false)
    private String priority; // LOW, MEDIUM, HIGH

    @Column(name = "resolution_time_hours", nullable = false)
    private int resolutionTimeHours;

    public SLA() {
    }

    public SLA(Long slaId, String priority, int resolutionTimeHours) {
        this.slaId = slaId;
        this.priority = priority;
        this.resolutionTimeHours = resolutionTimeHours;
    }

    public Long getSlaId() {
        return slaId;
    }

    public void setSlaId(Long slaId) {
        this.slaId = slaId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getResolutionTimeHours() {
        return resolutionTimeHours;
    }

    public void setResolutionTimeHours(int resolutionTimeHours) {
        this.resolutionTimeHours = resolutionTimeHours;
    }

}
