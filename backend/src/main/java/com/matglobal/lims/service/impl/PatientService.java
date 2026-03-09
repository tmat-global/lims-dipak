package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.PatientRequest;
import com.matglobal.lims.dto.response.PatientResponse;
import com.matglobal.lims.entity.Patient;
import com.matglobal.lims.exception.*;
import com.matglobal.lims.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientResponse create(PatientRequest req) {
        Patient p = Patient.builder()
                .salutation(req.getSalutation()).name(req.getName())
                .gender(req.getGender()).age(req.getAge()).ageUnit(req.getAgeUnit())
                .dateOfBirth(req.getDateOfBirth()).mobile(req.getMobile())
                .alternateMobile(req.getAlternateMobile()).email(req.getEmail())
                .address(req.getAddress()).remarks(req.getRemarks())
                .passportNo(req.getPassportNo()).build();
        return toResponse(patientRepository.save(p));
    }

    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        return toResponse(patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id)));
    }

    @Transactional(readOnly = true)
    public PatientResponse findByMobile(String mobile) {
        return toResponse(patientRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("No patient found with mobile: " + mobile)));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> search(String name, String mobile, int page, int size) {
        return patientRepository.searchPatients(name, mobile, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    public PatientResponse update(Long id, PatientRequest req) {
        Patient p = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        p.setSalutation(req.getSalutation()); p.setName(req.getName());
        p.setAge(req.getAge()); p.setAgeUnit(req.getAgeUnit());
        p.setDateOfBirth(req.getDateOfBirth()); p.setMobile(req.getMobile());
        p.setAlternateMobile(req.getAlternateMobile()); p.setEmail(req.getEmail());
        p.setAddress(req.getAddress()); p.setRemarks(req.getRemarks());
        return toResponse(patientRepository.save(p));
    }

    private PatientResponse toResponse(Patient p) {
        List<PatientResponse.RegistrationSummary> regs = p.getRegistrations() == null ? List.of() :
                p.getRegistrations().stream().map(r -> PatientResponse.RegistrationSummary.builder()
                        .id(r.getId()).regNo(r.getRegNo()).status(r.getStatus().name())
                        .totalAmount(r.getTotalAmount()).createdAt(r.getCreatedAt()).build())
                        .collect(Collectors.toList());
        return PatientResponse.builder()
                .id(p.getId()).salutation(p.getSalutation()).name(p.getName())
                .gender(p.getGender()).age(p.getAge()).ageUnit(p.getAgeUnit())
                .dateOfBirth(p.getDateOfBirth()).mobile(p.getMobile())
                .alternateMobile(p.getAlternateMobile()).email(p.getEmail())
                .address(p.getAddress()).remarks(p.getRemarks()).passportNo(p.getPassportNo())
                .createdAt(p.getCreatedAt()).registrations(regs).build();
    }
}
