package com.htc.incidentmanagement.service;



import java.util.List;
import com.htc.incidentmanagement.model.SLA;

public interface SLAService {

    List<SLA> getAllSlas();

    SLA getSlaById(Long id);

    SLA createSla(SLA sla);

    SLA updateSla(Long id, SLA sla);

    void deleteSla(Long id);
}
