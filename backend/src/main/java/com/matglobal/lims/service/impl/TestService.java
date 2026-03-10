package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.TestRequest;
import com.matglobal.lims.dto.response.TestResponse;
import com.matglobal.lims.entity.Test;
import com.matglobal.lims.exception.BusinessException;
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

    public TestResponse create(TestRequest req) {
        if (testRepository.existsByCode(req.getCode()))
            throw new DuplicateResourceException("Test code already exists: " + req.getCode());
        Test t = new Test();
        t.setCode(req.getCode()); t.setName(req.getName()); t.setType(req.getType());
        t.setDepartment(req.getDepartment()); t.setRate(req.getRate());
        t.setDescription(req.getDescription()); t.setSampleType(req.getSampleType());
        t.setTurnaroundHours(req.getTurnaroundHours()); t.setIsActive(true);
        return toResponse(testRepository.save(t));
    }

    @Transactional(readOnly = true)
    public List<TestResponse> findAll() {
        return testRepository.findByIsActiveTrue().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestResponse> search(String q) {
        return testRepository.searchActive(q).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TestResponse update(Long id, TestRequest req) {
        Test t = testRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Test", id));
        t.setName(req.getName()); t.setType(req.getType()); t.setDepartment(req.getDepartment());
        t.setRate(req.getRate()); t.setDescription(req.getDescription());
        t.setSampleType(req.getSampleType()); t.setTurnaroundHours(req.getTurnaroundHours());
        if (req.getIsActive() != null) t.setIsActive(req.getIsActive());
        return toResponse(testRepository.save(t));
    }

    public void delete(Long id) {
        Test t = testRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Test", id));
        t.setIsActive(false);
        testRepository.save(t);
    }

    public TestResponse toResponse(Test t) {
        TestResponse r = new TestResponse();
        r.setId(t.getId()); r.setCode(t.getCode()); r.setName(t.getName());
        r.setType(t.getType()); r.setDepartment(t.getDepartment()); r.setRate(t.getRate());
        r.setDescription(t.getDescription()); r.setSampleType(t.getSampleType());
        r.setTurnaroundHours(t.getTurnaroundHours()); r.setIsActive(t.getIsActive());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}
