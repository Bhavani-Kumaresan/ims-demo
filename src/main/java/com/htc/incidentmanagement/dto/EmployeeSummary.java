package com.htc.incidentmanagement.dto;

import com.htc.incidentmanagement.model.Employee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary employee info ")
public class EmployeeSummary {
    @Schema(example = "1")
    private Long employeeId;

    @Schema(example = "Carol User")
    private String name;

    @Schema(example = "carol.user@htc.com")
    private String email;

    @Schema(example = "USER")
    private String role;

    // Constructor from Employee entity
    public EmployeeSummary(Employee employee) {
        this.employeeId = employee.getEmployeeId();
        this.name = employee.getName();
        this.email = employee.getEmail();
        this.role = employee.getRole();
    }

    public EmployeeSummary() {
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Getters
    public Long getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
