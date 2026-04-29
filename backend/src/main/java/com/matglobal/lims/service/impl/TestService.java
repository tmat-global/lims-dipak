package com.matglobal.lims.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matglobal.lims.dto.request.TestRequest;
import com.matglobal.lims.dto.response.TestResponse;
import com.matglobal.lims.entity.Test;
import com.matglobal.lims.exception.DuplicateResourceException;
import com.matglobal.lims.exception.ResourceNotFoundException;
import com.matglobal.lims.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TestService {

    private final TestRepository testRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TestResponse create(TestRequest req) {
        if (testRepository.existsByCode(req.getCode()))
            throw new DuplicateResourceException("Test code already exists: " + req.getCode());
        Test t = mapToEntity(new Test(), req);
        return toResponse(testRepository.save(t));
    }

    @Transactional(readOnly = true)
    public List<TestResponse> findAll() {
        return testRepository.findByIsActiveTrue()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestResponse> search(String q) {
        return testRepository.searchActive(q)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TestResponse update(Long id, TestRequest req) {
        Test t = testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test", id));
        mapToEntity(t, req);
        return toResponse(testRepository.save(t));
    }

    public void delete(Long id) {
        Test t = testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test", id));
        t.setIsActive(false);
        testRepository.save(t);
    }

    private Test mapToEntity(Test t, TestRequest req) {
        t.setCode(req.getCode().toUpperCase().trim());
        t.setName(req.getName());
        t.setType(req.getType() != null ? req.getType() : "Test");
        t.setDepartment(req.getDepartment());
        t.setRate(req.getRate());
        t.setDescription(req.getDescription());
        t.setSampleType(req.getSampleType());
        t.setTurnaroundHours(req.getTurnaroundHours());
        if (req.getIsActive() != null) t.setIsActive(req.getIsActive());
        else t.setIsActive(true);
        // Store packageTests as JSON string in description field for packages
        if (req.getPackageTests() != null && !req.getPackageTests().isEmpty()) {
            try {
                t.setTestConfig(objectMapper.writeValueAsString(req.getPackageTests()));
            } catch (JsonProcessingException e) {
                t.setTestConfig("[]");
            }
        }
        if (req.getParameters() != null) t.setParameters(req.getParameters());
        if (req.getRanges()     != null) t.setRanges(req.getRanges());
        if (req.getFormula()    != null) t.setFormula(req.getFormula());
        if (req.getReportNotes()!= null) t.setReportNotes(req.getReportNotes());
        return t;
    }

    public TestResponse toResponse(Test t) {
        TestResponse r = new TestResponse();
        r.setId(t.getId());
        r.setCode(t.getCode());
        r.setName(t.getName());
        r.setType(t.getType());
        r.setDepartment(t.getDepartment());
        r.setRate(t.getRate());
        r.setDescription(t.getDescription());
        r.setSampleType(t.getSampleType());
        r.setTurnaroundHours(t.getTurnaroundHours());
        r.setIsActive(t.getIsActive());
        r.setCreatedAt(t.getCreatedAt());
        r.setParameters(t.getParameters());
        r.setRanges(t.getRanges());
        r.setFormula(t.getFormula());
        r.setReportNotes(t.getReportNotes());
        r.setTestConfig(t.getTestConfig());
        return r;
    }
}
