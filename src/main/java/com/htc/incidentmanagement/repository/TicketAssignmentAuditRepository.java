package com.htc.incidentmanagement.repository;

import com.htc.incidentmanagement.model.TicketAssignmentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketAssignmentAuditRepository extends JpaRepository<TicketAssignmentAudit, Long> {

    List<TicketAssignmentAudit> findByTicket_TicketIdOrderByAssignedAtDesc(Long ticketId);

}
