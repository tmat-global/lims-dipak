package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.PatientRequest;
import com.matglobal.lims.dto.response.PatientResponse;
import com.matglobal.lims.entity.Patient;
import com.matglobal.lims.exception.ResourceNotFoundException;
import com.matglobal.lims.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    // ✅ CREATE
    public PatientResponse create(PatientRequest req) {
        Patient p = new Patient();
        p.setSalutation(req.getSalutation());
        p.setName(req.getName());
        p.setGender(req.getGender());
        p.setAge(req.getAge());
        p.setAgeUnit(req.getAgeUnit());
        p.setDateOfBirth(req.getDateOfBirth());
        p.setMobile(req.getMobile());
        p.setAlternateMobile(req.getAlternateMobile());
        p.setEmail(req.getEmail());
        p.setAddress(req.getAddress());
        p.setRemarks(req.getRemarks());
        p.setPassportNo(req.getPassportNo());

        return toResponse(patientRepository.save(p));
    }

    // ✅ GET BY ID
    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        return toResponse(patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id)));
    }

    // ✅ GET BY MOBILE
    @Transactional(readOnly = true)
    public PatientResponse findByMobile(String mobile) {
        return toResponse(patientRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("No patient found with mobile: " + mobile)));
    }

    // ✅ SEARCH (Pagination + Filter)
    @Transactional(readOnly = true)
    public Page<PatientResponse> search(String name, String mobile, int page, int size) {
        return patientRepository.searchPatients(
                name,
                mobile,
                PageRequest.of(page, size, Sort.by("createdAt").descending())).map(this::toResponse);
    }

    // ✅ UPDATE
    public PatientResponse update(Long id, PatientRequest req) {
        Patient p = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));

        p.setSalutation(req.getSalutation());
        p.setName(req.getName());
        p.setGender(req.getGender()); // ✅ FIXED (was missing)
        p.setAge(req.getAge());
        p.setAgeUnit(req.getAgeUnit());
        p.setDateOfBirth(req.getDateOfBirth());
        p.setMobile(req.getMobile());
        p.setAlternateMobile(req.getAlternateMobile());
        p.setEmail(req.getEmail());
        p.setAddress(req.getAddress());
        p.setRemarks(req.getRemarks());
        p.setPassportNo(req.getPassportNo());

        return toResponse(patientRepository.save(p));
    }

    // ✅ DELETE (🔥 THIS FIXES YOUR BUILD ERROR)
    public void delete(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));

        patientRepository.delete(patient);
    }

    // ✅ MAPPER (Entity → Response)
    private PatientResponse toResponse(Patient p) {

        List<PatientResponse.RegistrationSummary> regs = new ArrayList<>();

        if (p.getRegistrations() != null) {
            regs = p.getRegistrations().stream().map(r -> {
                PatientResponse.RegistrationSummary s = new PatientResponse.RegistrationSummary();
                s.setId(r.getId());
                s.setRegNo(r.getRegNo());
                s.setStatus(r.getStatus().name());
                s.setTotalAmount(r.getTotalAmount());
                s.setCreatedAt(r.getCreatedAt());
                return s;
            }).collect(Collectors.toList());
        }

        PatientResponse r = new PatientResponse();
        r.setId(p.getId());
        r.setSalutation(p.getSalutation());
        r.setName(p.getName());
        r.setGender(p.getGender());
        r.setAge(p.getAge());
        r.setAgeUnit(p.getAgeUnit());
        r.setDateOfBirth(p.getDateOfBirth());
        r.setMobile(p.getMobile());
        r.setAlternateMobile(p.getAlternateMobile());
        r.setEmail(p.getEmail());
        r.setAddress(p.getAddress());
        r.setRemarks(p.getRemarks());
        r.setPassportNo(p.getPassportNo());
        r.setCreatedAt(p.getCreatedAt());
        r.setRegistrations(regs);

        return r;
    }
}