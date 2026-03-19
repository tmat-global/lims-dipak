package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.ReferringDoctorRequest;
import com.matglobal.lims.dto.response.ReferringDoctorResponse;
import com.matglobal.lims.entity.ReferringDoctor;
import com.matglobal.lims.exception.DuplicateResourceException;
import com.matglobal.lims.exception.ResourceNotFoundException;
import com.matglobal.lims.repository.ReferringDoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReferringDoctorService {
    private final ReferringDoctorRepository repo;

    public ReferringDoctorResponse create(ReferringDoctorRequest req) {
        String code = (req.getCode() != null && !req.getCode().isBlank()) ? req.getCode() : generateCode();
        if (repo.existsByCode(code)) throw new DuplicateResourceException("Doctor code already exists");
        ReferringDoctor d = new ReferringDoctor();
        d.setCode(code); d.setName(req.getName()); d.setMobile(req.getMobile());
        d.setEmail(req.getEmail()); d.setAddress(req.getAddress()); d.setCity(req.getCity());
        d.setPatientType(req.getPatientType()); d.setRateType(req.getRateType()); d.setIsActive(true);
        return toResponse(repo.save(d));
    }

    @Transactional(readOnly = true)
    public List<ReferringDoctorResponse> findAll() {
        return repo.findByIsActiveTrueOrderByNameAsc().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ReferringDoctorResponse update(Long id, ReferringDoctorRequest req) {
        ReferringDoctor d = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        d.setName(req.getName()); d.setMobile(req.getMobile()); d.setEmail(req.getEmail());
        d.setAddress(req.getAddress()); d.setCity(req.getCity());
        d.setPatientType(req.getPatientType()); d.setRateType(req.getRateType());
        return toResponse(repo.save(d));
    }

    public void delete(Long id) {
        ReferringDoctor d = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        d.setIsActive(false); repo.save(d);
    }

    private String generateCode() {
        return String.format("%03d", repo.count() + 101);
    }

    public ReferringDoctorResponse toResponse(ReferringDoctor d) {
        ReferringDoctorResponse r = new ReferringDoctorResponse();
        r.setId(d.getId()); r.setCode(d.getCode()); r.setName(d.getName());
        r.setMobile(d.getMobile()); r.setEmail(d.getEmail()); r.setAddress(d.getAddress());
        r.setCity(d.getCity()); r.setPatientType(d.getPatientType()); r.setRateType(d.getRateType());
        r.setIsActive(d.getIsActive()); r.setCreatedAt(d.getCreatedAt());
        return r;
    }
}
