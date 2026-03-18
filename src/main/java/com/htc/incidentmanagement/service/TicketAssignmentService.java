package com.htc.incidentmanagement.service;

import java.util.List;

import com.htc.incidentmanagement.dto.TicketAssignmentResponse;

public interface TicketAssignmentService {

    List<TicketAssignmentResponse> getAssignmentsByTicket(Long ticketId);

    TicketAssignmentResponse createAssignment(Long ticketId, Long employeeId, String comment);

    void deleteAssignmentsByTicket(Long ticketId);

}
