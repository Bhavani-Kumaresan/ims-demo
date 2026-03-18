package com.htc.incidentmanagement.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.htc.incidentmanagement.dto.CategorySummary;
import com.htc.incidentmanagement.dto.EmployeeSummary;
import com.htc.incidentmanagement.dto.SLASummary;
import com.htc.incidentmanagement.dto.TicketAssignmentAuditRecord;
import com.htc.incidentmanagement.dto.TicketResponse;
import com.htc.incidentmanagement.model.Category;
import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.IncidentTicket;
import com.htc.incidentmanagement.model.SLA;
import com.htc.incidentmanagement.serviceImpl.TicketAssignmentAuditServiceImpl;

@Component
public class TicketMapper {

    private final TicketAssignmentAuditServiceImpl assignmentAudit;

    public TicketMapper(TicketAssignmentAuditServiceImpl assignmentAudit) {
        this.assignmentAudit = assignmentAudit;
    }

    public TicketResponse mapToResponse(IncidentTicket ticket) {

        // Fetch audit records from service
        List<TicketAssignmentAuditRecord> auditRecords = assignmentAudit
                .getAssignmentHistoryByTicketId(ticket.getTicketId())
                .stream()
                .map(a -> new TicketAssignmentAuditRecord(
                        a.getAuditId(),
                        new EmployeeSummary(a.getAssignedTo()),
                        new EmployeeSummary(a.getAssignedBy()),
                        a.getAssignedAt(),
                        a.getUpdatedAt(),
                        a.getClosedAt()))
                .toList();

        // Determine the current assigned employee
        EmployeeSummary currentAssignee = null;
        if (!auditRecords.isEmpty()) {
            // Most recent assignment
            currentAssignee = auditRecords.get(auditRecords.size() - 1).assignedTo();
        } else if (ticket.getAssignedTo() != null) {
            // Fallback to ticket.assignedTo
            currentAssignee = mapEmployee(ticket.getAssignedTo());
        }

        TicketResponse response = new TicketResponse(ticket, auditRecords);
        response.setAssignedTo(currentAssignee); // ensure assignedTo reflects latest assignment

        return response;
    }

    private EmployeeSummary mapEmployee(Employee employee) {
        if (employee == null)
            return null;

        EmployeeSummary dto = new EmployeeSummary();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setRole(employee.getRole());
        return dto;
    }

    private CategorySummary mapCategory(Category category) {
        if (category == null)
            return null;

        CategorySummary dto = new CategorySummary();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        return dto;
    }

    private SLASummary mapSla(SLA sla) {
        if (sla == null)
            return null;

        SLASummary dto = new SLASummary();
        dto.setSlaId(sla.getSlaId());
        dto.setPriority(sla.getPriority());
        dto.setResolutionTimeHours(sla.getResolutionTimeHours());
        return dto;
    }
}
