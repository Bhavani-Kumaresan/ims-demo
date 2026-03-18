package com.htc.incidentmanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.htc.incidentmanagement.model.SLA;

@Repository
public interface SLARepository extends JpaRepository<SLA, Long> {

    boolean existsByPriority(String priority);
}
