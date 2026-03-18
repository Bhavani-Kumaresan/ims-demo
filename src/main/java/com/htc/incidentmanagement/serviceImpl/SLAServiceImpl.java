package com.htc.incidentmanagement.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htc.incidentmanagement.model.SLA;
import com.htc.incidentmanagement.repository.SLARepository;
import com.htc.incidentmanagement.service.SLAService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class SLAServiceImpl implements SLAService {

    private static final Logger logger = LoggerFactory.getLogger(SLAServiceImpl.class);

    private final SLARepository slaRepository;

    public SLAServiceImpl(SLARepository slaRepository) {
        this.slaRepository = slaRepository;
        logger.info("SLAService initialized.");
    }

    @Override
    public List<SLA> getAllSlas() {
        return slaRepository.findAll();
    }

    @Override
    public SLA getSlaById(Long id) {
        return slaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SLA not found: " + id));
    }

    @Override
    public SLA createSla(SLA sla) {
        if (slaRepository.existsByPriority(sla.getPriority())) {
            throw new RuntimeException("SLA priority already exists: " + sla.getPriority());
        }
        return slaRepository.save(sla);
    }

    @Override
    public SLA updateSla(Long id, SLA sla) {
        SLA existing = getSlaById(id);
        existing.setPriority(sla.getPriority());
        existing.setResolutionTimeHours(sla.getResolutionTimeHours());
        return slaRepository.save(existing);
    }

    @Override
    public void deleteSla(Long id) {
        SLA existing = getSlaById(id);
        slaRepository.delete(existing);
    }
}
