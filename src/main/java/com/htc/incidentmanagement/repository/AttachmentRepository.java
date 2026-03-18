package com.htc.incidentmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.htc.incidentmanagement.model.Attachment;
import com.htc.incidentmanagement.model.IncidentTicket;

import jakarta.transaction.Transactional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByTicket(IncidentTicket ticket);

    @Transactional
    void deleteAllByTicket(IncidentTicket ticket);
}
