package com.htc.incidentmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.IncidentTicket;

@Repository
public interface IncidentTicketRepository extends JpaRepository<IncidentTicket, Long> {

  List<IncidentTicket> findByCreatedByEmployeeId(Long employeeId);

  List<IncidentTicket> findByAssignedToEmployeeId(Long employeeId);

  List<IncidentTicket> findByStatus(String status);

  @Query("SELECT t FROM IncidentTicket t WHERE t.assignedTo.employeeId = :employeeId")
  List<IncidentTicket> findByAssignedEmployeeId(Long employeeId);

  List<IncidentTicket> findByAssignedTo(Employee employee);

}
