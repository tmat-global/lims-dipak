package com.matglobal.lims.controller;

import com.matglobal.lims.dto.response.ApiResponse;
import com.matglobal.lims.entity.RoutineTest;
import com.matglobal.lims.repository.RoutineTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/routine-tests")
@RequiredArgsConstructor
public class RoutineTestController {

    private final RoutineTestRepository routineTestRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> findAll() {
        List<Map<String,Object>> result = routineTestRepository
            .findByIsActiveTrueOrderByTestNameAsc()
            .stream().map(r -> {
                Map<String,Object> m = new LinkedHashMap<>();
                m.put("id", r.getId());
                m.put("testCode", r.getTestCode());
                m.put("testName", r.getTestName());
                m.put("testId",   r.getTestId());
                return m;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<Map<String,Object>>> create(
            @RequestBody Map<String,Object> body) {
        Long testId = body.get("testId") != null ?
            Long.valueOf(body.get("testId").toString()) : null;
        if (testId != null && routineTestRepository.existsByTestIdAndIsActiveTrue(testId)) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Test already in routine list"));
        }
        RoutineTest rt = RoutineTest.builder()
            .testCode((String) body.get("testCode"))
            .testName((String) body.get("testName"))
            .testId(testId)
            .isActive(true).build();
        routineTestRepository.save(rt);
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id", rt.getId());
        m.put("testCode", rt.getTestCode());
        m.put("testName", rt.getTestName());
        return ResponseEntity.ok(ApiResponse.ok("Added to routine", m));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<String>> update(
            @PathVariable Long id, @RequestBody Map<String,Object> body) {
        routineTestRepository.findById(id).ifPresent(rt -> {
            if (body.get("testCode") != null) rt.setTestCode((String) body.get("testCode"));
            if (body.get("testName") != null) rt.setTestName((String) body.get("testName"));
            routineTestRepository.save(rt);
        });
        return ResponseEntity.ok(ApiResponse.ok("Updated", null));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        routineTestRepository.findById(id).ifPresent(rt -> {
            rt.setIsActive(false);
            routineTestRepository.save(rt);
        });
        return ResponseEntity.ok(ApiResponse.ok("Deleted", null));
    }
}
