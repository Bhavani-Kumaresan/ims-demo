package com.htc.incidentmanagement.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htc.incidentmanagement.dto.CreateTicketRequest;
import com.htc.incidentmanagement.dto.EmployeeSummary;
import com.htc.incidentmanagement.dto.TicketAssignmentAuditRecord;
import com.htc.incidentmanagement.dto.TicketResponse;
import com.htc.incidentmanagement.exception.BusinessValidationException;
import com.htc.incidentmanagement.exception.ResourceNotFoundException;
import com.htc.incidentmanagement.model.Category;
import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.IncidentTicket;
import com.htc.incidentmanagement.model.SLA;
import com.htc.incidentmanagement.model.TicketAssignment;
import com.htc.incidentmanagement.repository.AttachmentRepository;
import com.htc.incidentmanagement.repository.CategoryRepository;
import com.htc.incidentmanagement.repository.EmployeeRepository;
import com.htc.incidentmanagement.repository.IncidentTicketRepository;
import com.htc.incidentmanagement.repository.SLARepository;
import com.htc.incidentmanagement.repository.TicketAssignmentRepository;
import com.htc.incidentmanagement.security.SecurityUtil;
import com.htc.incidentmanagement.service.IncidentTicketService;

@Service
@Transactional
public class IncidentTicketServiceImpl implements IncidentTicketService {

    private static final Logger logger = LoggerFactory.getLogger(IncidentTicketServiceImpl.class);

    private final IncidentTicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final CategoryRepository categoryRepository;
    private final SLARepository slaRepository;
    private final TicketAssignmentRepository assignmentRepository;
    private final AttachmentRepository attachmentRepository;
    private final TicketAssignmentAuditServiceImpl assignmentAudit;

    public IncidentTicketServiceImpl(IncidentTicketRepository ticketRepository,
            EmployeeRepository employeeRepository,
            CategoryRepository categoryRepository,
            SLARepository slaRepository,
            TicketAssignmentRepository assignmentRepository, AttachmentRepository attachmentRepository,
            TicketAssignmentAuditServiceImpl assignmentAudit) {
        this.ticketRepository = ticketRepository;
        this.employeeRepository = employeeRepository;
        this.categoryRepository = categoryRepository;
        this.slaRepository = slaRepository;
        this.assignmentRepository = assignmentRepository;
        this.attachmentRepository = attachmentRepository;
        this.assignmentAudit = assignmentAudit;
        logger.info("IncidentTicketService initialized");
    }

    @Override
    public TicketResponse createTicket(
            CreateTicketRequest request,
            Long createdById,
            Long categoryId,
            Long slaId) {

        logger.info("Creating ticket | createdBy={}, category={}, sla={}",
                createdById, categoryId, slaId);

        validateTicketRequest(request);

        Employee creator = employeeRepository.findById(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", createdById));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

        SLA sla = slaRepository.findById(slaId)
                .orElseThrow(() -> new ResourceNotFoundException("SLA", slaId));

        IncidentTicket ticket = new IncidentTicket();
        ticket.setTitle(request.getTitle());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(
                request.getStatus() != null ? request.getStatus() : "OPEN");
        ticket.setCreatedBy(creator);
        ticket.setAssignedTo(null);
        ticket.setCategory(category);
        ticket.setSla(sla);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setApproved(false);
        ticket.setClosedBy(null);

        IncidentTicket saved = ticketRepository.save(ticket);

        logger.info("Ticket created successfully | ticketId={}",
                saved.getTicketId());

        return mapToResponse(saved);
    }

    @Override
    public IncidentTicket assignTicket(Long ticketId, Long employeeId, String comment) {
        logger.info("Assigning ticket {} to employee {}", ticketId, employeeId);

        IncidentTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));

        if (!Boolean.TRUE.equals(ticket.getApproved())) {
            throw new BusinessValidationException(
                    "Cannot assign ticket ID " + ticketId + " because it is not approved.");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));

        if (!employee.getRole().toUpperCase().equals("AGENT")) {
            logger.info("Employee role:: " + employee.getRole());
            throw new BusinessValidationException(
                    "Cannot assign ticket ID " + ticketId + " because the Employee is not an AGENT.");
        }

        if ("CLOSED".equals(ticket.getStatus().toUpperCase())) {
            logger.warn("Cannot assign closed ticket: {}", ticketId);
            throw new BusinessValidationException("Cannot assign closed ticket");
        }

        // Update ticket
        ticket.setAssignedTo(employee);
        ticket.setStatus("IN_PROGRESS");

        // Create assignment record
        TicketAssignment assignment = new TicketAssignment();
        assignment.setTicket(ticket);
        assignment.setEmployee(employee);
        assignment.setComment(comment != null ? comment : "Assigned by system");
        assignment.setAssignedAt(LocalDateTime.now());

        assignmentRepository.save(assignment);
        logger.info("Ticket {} assigned to {} - Comment: {}", ticketId, employee.getEmail(), comment);

        assignmentAudit.logTicketAssignment(ticket, employee, SecurityUtil.getCurrentUser());

        return ticketRepository.save(ticket);
    }

    @Override
    @PostFilter("hasRole('ADMIN') or filterObject.createdBy.email == authentication.name")
    public List<IncidentTicket> getAllTickets() {
        logger.debug("Fetching all tickets");
        List<IncidentTicket> tickets = ticketRepository.findAll();
        logger.info("Retrieved {} tickets", tickets.size());
        return tickets;
    }

    @Override
    public IncidentTicket getTicketById(Long ticketId) {
        logger.debug("Fetching ticket ID: {}", ticketId);
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));
    }

    @Override
    public List<IncidentTicket> getTicketsByStatus(String status) {
        logger.debug("Fetching tickets by status: {}", status);
        List<IncidentTicket> tickets = ticketRepository.findByStatus(status);
        logger.info("Found {} tickets with status: {}", tickets.size(), status);
        return tickets;
    }

    private void validateTicketRequest(CreateTicketRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessValidationException("Ticket title is required");
        }
        List<String> validPriorities = List.of("HIGH", "MEDIUM", "LOW");
        if (!validPriorities.contains(request.getPriority())) {
            throw new BusinessValidationException("Invalid priority. Must be HIGH, MEDIUM, or LOW");
        }
    }

    @Override
    public IncidentTicket approveTicket(Long ticketId) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + ticketId));

        ticket.setApproved(true);
        // ticket.setStatus("APPROVED");

        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void deleteTicket(Long ticketId) {
        IncidentTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        // Delete associated ticket assignments
        assignmentRepository.deleteAllByTicket(ticket);

        // Delete associated attachments
        attachmentRepository.deleteAllByTicket(ticket);

        // Delete the ticket itself
        ticketRepository.delete(ticket);
    }

    @Override
    public TicketResponse closeTicket(Long ticketId, Long closedByEmployeeId) {

        logger.info("Closing ticket | ticketId={}, closedBy={}",
                ticketId, closedByEmployeeId);

        IncidentTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));

        if (!ticket.getApproved()) {
            throw new BusinessValidationException("Cannot close ticket. Ticket is not approved.");
        }

        Employee closer = employeeRepository.findById(closedByEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", closedByEmployeeId));

        if (!ticket.getApproved()) {
            throw new BusinessValidationException(
                    "Ticket must be approved before closing");
        }

        if ("CLOSED".equalsIgnoreCase(ticket.getStatus())) {
            throw new BusinessValidationException(
                    "Ticket is already closed");
        }

        ticket.setStatus("CLOSED");
        ticket.setClosedAt(LocalDateTime.now());
        ticket.setClosedBy(closer);

        IncidentTicket saved = ticketRepository.save(ticket);

        logger.info("Ticket closed successfully | ticketId={}", ticketId);

        return mapToResponse(saved);
    }

    @Override
    public List<TicketResponse> getTicketsAssignedToEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));

        List<IncidentTicket> tickets = ticketRepository.findByAssignedTo(employee);

        return tickets.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ----------------------------------------------------
    // MAPPING LOGIC (FIXES createdBy / assignedTo ISSUE)
    // ----------------------------------------------------
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

    // private CategorySummary mapCategory(Category category) {
    // if (category == null)
    // return null;

    // CategorySummary dto = new CategorySummary();
    // dto.setCategoryId(category.getCategoryId());
    // dto.setCategoryName(category.getCategoryName());
    // return dto;
    // }

    // private SLASummary mapSla(SLA sla) {
    // if (sla == null)
    // return null;

    // SLASummary dto = new SLASummary();
    // dto.setSlaId(sla.getSlaId());
    // dto.setPriority(sla.getPriority());
    // dto.setResolutionTimeHours(sla.getResolutionTimeHours());
    // return dto;
    // }

}
