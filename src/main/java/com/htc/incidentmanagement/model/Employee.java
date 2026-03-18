package com.htc.incidentmanagement.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "ims_employees")
@Schema(description = "Employee entity representing system users with roles")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "email is required")
    private String email;

    @Column(nullable = false)
    private String role; // ADMIN, AGENT, USER, MANAGER

    @OneToMany(mappedBy = "createdBy")
    private List<IncidentTicket> createdTickets;

    @OneToMany(mappedBy = "assignedTo")
    private List<IncidentTicket> assignedTickets;

    @OneToMany(mappedBy = "employee")
    private List<TicketAssignment> assignments;

    public Employee(Long employeeId, String name, String email, String role, List<IncidentTicket> createdTickets,
            List<IncidentTicket> assignedTickets, List<TicketAssignment> assignments) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdTickets = createdTickets;
        this.assignedTickets = assignedTickets;
        this.assignments = assignments;
    }

    public Employee() {
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<IncidentTicket> getCreatedTickets() {
        return createdTickets;
    }

    public void setCreatedTickets(List<IncidentTicket> createdTickets) {
        this.createdTickets = createdTickets;
    }

    public List<IncidentTicket> getAssignedTickets() {
        return assignedTickets;
    }

    public void setAssignedTickets(List<IncidentTicket> assignedTickets) {
        this.assignedTickets = assignedTickets;
    }

    public List<TicketAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<TicketAssignment> assignments) {
        this.assignments = assignments;
    }

}
