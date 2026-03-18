package com.htc.incidentmanagement.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htc.incidentmanagement.exception.ResourceNotFoundException;
import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.IncidentTicket;
import com.htc.incidentmanagement.model.TicketAssignmentAudit;
import com.htc.incidentmanagement.repository.EmployeeRepository;
import com.htc.incidentmanagement.repository.TicketAssignmentAuditRepository;
import com.htc.incidentmanagement.security.EmployeeUserDetails;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TicketAssignmentAuditServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(TicketAssignmentAuditServiceImpl.class);

    @Autowired
    private TicketAssignmentAuditRepository auditRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public TicketAssignmentAuditServiceImpl(TicketAssignmentAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
        logger.info("TicketAssignmentAuditService initialized.");
    }

    public List<TicketAssignmentAudit> getAssignmentHistoryByTicketId(Long ticketId) {
        return auditRepository.findByTicket_TicketIdOrderByAssignedAtDesc(ticketId);

    }

    public void logTicketAssignment(IncidentTicket ticket, Employee assignedTo,
            EmployeeUserDetails currentUserDetails) {

        Employee assignedBy = employeeRepository.findById(currentUserDetails.getEmploeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", currentUserDetails.getEmploeeId()));

        TicketAssignmentAudit audit = new TicketAssignmentAudit();
        audit.setTicket(ticket);
        audit.setAssignedTo(assignedTo);
        audit.setAssignedBy(assignedBy);
        audit.setAssignedAt(LocalDateTime.now());

        auditRepository.save(audit);
    }

}
