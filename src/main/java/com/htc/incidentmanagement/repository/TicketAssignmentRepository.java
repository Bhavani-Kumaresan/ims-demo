package com.htc.incidentmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.htc.incidentmanagement.model.TicketAssignment;

import jakarta.transaction.Transactional;

import com.htc.incidentmanagement.model.IncidentTicket;

@Repository
public interface TicketAssignmentRepository extends JpaRepository<TicketAssignment, Long> {

    List<TicketAssignment> findByTicket(IncidentTicket ticket);

    @Transactional
    void deleteAllByTicket(IncidentTicket ticket);

}
