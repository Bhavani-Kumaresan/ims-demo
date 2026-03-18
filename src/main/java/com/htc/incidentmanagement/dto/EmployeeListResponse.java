package com.htc.incidentmanagement.dto;


import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "List of employees response")
public class EmployeeListResponse {
    
    @Schema(description = "Total count of employees")
    private long totalCount;
    
    @Schema(description = "List of employee summaries")
    private List<EmployeeSummary> employees;
    
    public EmployeeListResponse(List<com.htc.incidentmanagement.model.Employee> employees) {
        this.totalCount = employees.size();
        this.employees = employees.stream()
                .map(EmployeeSummary::new)
                .collect(Collectors.toList());
    }
    
    // Getters & Setters
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    
    public List<EmployeeSummary> getEmployees() { return employees; }
    public void setEmployees(List<EmployeeSummary> employees) { this.employees = employees; }
}
