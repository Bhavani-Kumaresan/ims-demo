package com.htc.incidentmanagement.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htc.incidentmanagement.dto.TicketAssignmentResponse;
import com.htc.incidentmanagement.exception.BusinessValidationException;
import com.htc.incidentmanagement.exception.ResourceNotFoundException;
import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.IncidentTicket;
import com.htc.incidentmanagement.model.TicketAssignment;
import com.htc.incidentmanagement.repository.EmployeeRepository;
import com.htc.incidentmanagement.repository.IncidentTicketRepository;
import com.htc.incidentmanagement.repository.TicketAssignmentRepository;
import com.htc.incidentmanagement.service.TicketAssignmentService;

@Service
@Transactional
public class TicketAssignmentServiceImpl implements TicketAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(TicketAssignmentServiceImpl.class);

    private final TicketAssignmentRepository ticketAssignmentRepository;
    private final IncidentTicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;

    public TicketAssignmentServiceImpl(TicketAssignmentRepository ticketAssignmentRepository,
            IncidentTicketRepository ticketRepository,
            EmployeeRepository employeeRepository) {
        this.ticketAssignmentRepository = ticketAssignmentRepository;
        this.ticketRepository = ticketRepository;
        this.employeeRepository = employeeRepository;
        logger.info("TicketAssignmentService initialized.");
    }

    @Override
    public List<TicketAssignmentResponse> getAssignmentsByTicket(Long ticketId) {

        IncidentTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        List<TicketAssignment> assignmentEntities = ticketAssignmentRepository.findByTicket(ticket);

        List<TicketAssignmentResponse> assignmentResponses = assignmentEntities.stream().map(assignment -> {
            TicketAssignmentResponse response = new TicketAssignmentResponse();
            response.setAssignmentId(assignment.getAssignmentId());
            response.setTicketId(ticket.getTicketId());
            response.setTicketTitle(ticket.getTitle());
            response.setEmployeeId(assignment.getEmployee().getEmployeeId());
            response.setEmployeeName(assignment.getEmployee().getName());
            response.setComment(assignment.getComment());
            response.setAssignedAt(assignment.getAssignedAt());
            return response;
        }).toList();

        return assignmentResponses;
    }

    @Override
    public TicketAssignmentResponse createAssignment(Long ticketId, Long employeeId, String comment) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        if ("CLOSED".equals(ticket.getStatus().toUpperCase())) {
            logger.warn("Cannot assign closed ticket: {}", ticketId);
            throw new BusinessValidationException("Cannot assign closed ticket");
        }

        TicketAssignment assignment = new TicketAssignment();
        assignment.setTicket(ticket);
        assignment.setEmployee(employee);
        assignment.setComment(comment);
        assignment.setAssignedAt(LocalDateTime.now());

        ticket.setAssignedTo(employee);

        ticketAssignmentRepository.save(assignment);

        TicketAssignmentResponse response = new TicketAssignmentResponse();
        response.setAssignmentId(assignment.getAssignmentId());
        response.setTicketId(ticket.getTicketId());
        response.setTicketTitle(ticket.getTitle());
        response.setEmployeeId(employee.getEmployeeId());
        response.setEmployeeName(employee.getName());
        response.setComment(comment);
        response.setAssignedAt(assignment.getAssignedAt());

        return response;
    }

    @Override
    public void deleteAssignmentsByTicket(Long ticketId) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + ticketId));

        ticketAssignmentRepository.deleteAllByTicket(ticket);
    }

}
