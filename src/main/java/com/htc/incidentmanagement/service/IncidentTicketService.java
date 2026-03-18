package com.htc.incidentmanagement.service;

import java.util.List;
import com.htc.incidentmanagement.dto.CreateTicketRequest;
import com.htc.incidentmanagement.dto.TicketResponse;
import com.htc.incidentmanagement.model.IncidentTicket;

public interface IncidentTicketService {
    TicketResponse createTicket(CreateTicketRequest request, Long createdById, Long categoryId, Long slaId);

    IncidentTicket assignTicket(Long ticketId, Long employeeId, String comment);

    List<IncidentTicket> getAllTickets();

    IncidentTicket getTicketById(Long ticketId);

    List<IncidentTicket> getTicketsByStatus(String status);

    IncidentTicket approveTicket(Long ticketId);

    void deleteTicket(Long ticketId);

    TicketResponse closeTicket(Long ticketId, Long closedByEmployeeId);

    List<TicketResponse> getTicketsAssignedToEmployee(Long employeeId);

}