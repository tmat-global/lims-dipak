package com.matglobal.lims.controller;

import com.matglobal.lims.dto.response.ApiResponse;
import com.matglobal.lims.entity.TestCharge;
import com.matglobal.lims.repository.TestChargeRepository;
import com.matglobal.lims.repository.RateTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/test-charges")
@RequiredArgsConstructor
public class TestChargeController {

    private final TestChargeRepository testChargeRepository;
    private final RateTypeRepository rateTypeRepository;

    // GET all with optional filters
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String,Object>>> findAll(
            @RequestParam(required = false) String rateType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String search) {

        List<TestCharge> list;
        if (rateType != null && department != null) {
            list = testChargeRepository.findByRateTypeNameAndDepartmentAndIsActiveTrueOrderByTestNameAsc(rateType, department);
        } else if (rateType != null) {
            list = testChargeRepository.findByRateTypeNameAndIsActiveTrueOrderByTestNameAsc(rateType);
        } else {
            list = testChargeRepository.findByIsActiveTrueOrderByTestNameAsc();
        }

        // Apply search filter
        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            list = list.stream().filter(tc ->
                (tc.getTestName() != null && tc.getTestName().toLowerCase().contains(q)) ||
                (tc.getTestCode() != null && tc.getTestCode().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }

        List<Map<String,Object>> rows = list.stream().map(tc -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", tc.getId());
            m.put("rateTypeName", tc.getRateTypeName());
            m.put("department", tc.getDepartment());
            m.put("testCode", tc.getTestCode());
            m.put("testName", tc.getTestName());
            m.put("testId", tc.getTestId());
            m.put("amount", tc.getAmount());
            return m;
        }).collect(Collectors.toList());

        // Get rate type names and departments for dropdowns
        List<String> rateTypes = rateTypeRepository.findByIsActiveTrueOrderByCreatedAtDesc()
            .stream().map(r -> r.getName()).collect(Collectors.toList());
        List<String> departments = testChargeRepository.findDistinctDepartments();

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("charges", rows);
        result.put("rateTypes", rateTypes);
        result.put("departments", departments);
        result.put("totalCount", rows.size());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // Save/update multiple charges at once
    @PostMapping("/bulk")
    @Transactional
    public ResponseEntity<ApiResponse<String>> saveBulk(@RequestBody Map<String,Object> body) {
        String rateTypeName = (String) body.get("rateTypeName");
        String department   = (String) body.get("department");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> charges = (List<Map<String,Object>>) body.get("charges");

        if (rateTypeName == null || charges == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("rateTypeName and charges required"));
        }

        for (Map<String,Object> c : charges) {
            Long id = c.get("id") != null ? Long.valueOf(c.get("id").toString()) : null;
            String testCode = (String) c.get("testCode");
            String testName = (String) c.get("testName");
            Long testId     = c.get("testId") != null ? Long.valueOf(c.get("testId").toString()) : null;
            BigDecimal amt  = c.get("amount") != null ? new BigDecimal(c.get("amount").toString()) : BigDecimal.ZERO;
            String dept     = department != null ? department : (String) c.get("department");

            if (id != null) {
                testChargeRepository.findById(id).ifPresent(tc -> {
                    tc.setAmount(amt);
                    testChargeRepository.save(tc);
                });
            } else {
                TestCharge tc = TestCharge.builder()
                    .rateTypeName(rateTypeName).department(dept)
                    .testCode(testCode).testName(testName)
                    .testId(testId).amount(amt).isActive(true).build();
                testChargeRepository.save(tc);
            }
        }
        return ResponseEntity.ok(ApiResponse.ok("Charges saved successfully", null));
    }

    // Single save
    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<Map<String,Object>>> save(@RequestBody Map<String,Object> body) {
        TestCharge tc = TestCharge.builder()
            .rateTypeName((String) body.get("rateTypeName"))
            .department((String) body.get("department"))
            .testCode((String) body.get("testCode"))
            .testName((String) body.get("testName"))
            .testId(body.get("testId") != null ? Long.valueOf(body.get("testId").toString()) : null)
            .amount(body.get("amount") != null ? new BigDecimal(body.get("amount").toString()) : BigDecimal.ZERO)
            .isActive(true).build();
        testChargeRepository.save(tc);
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id", tc.getId()); m.put("rateTypeName", tc.getRateTypeName());
        m.put("testName", tc.getTestName()); m.put("amount", tc.getAmount());
        return ResponseEntity.ok(ApiResponse.ok("Saved", m));
    }

    // Update amount
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<String>> update(
            @PathVariable Long id, @RequestBody Map<String,Object> body) {
        testChargeRepository.findById(id).ifPresent(tc -> {
            if (body.get("amount") != null)
                tc.setAmount(new BigDecimal(body.get("amount").toString()));
            if (body.get("rateTypeName") != null) tc.setRateTypeName((String) body.get("rateTypeName"));
            if (body.get("department") != null) tc.setDepartment((String) body.get("department"));
            testChargeRepository.save(tc);
        });
        return ResponseEntity.ok(ApiResponse.ok("Updated", null));
    }

    // Delete
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        testChargeRepository.findById(id).ifPresent(tc -> {
            tc.setIsActive(false);
            testChargeRepository.save(tc);
        });
        return ResponseEntity.ok(ApiResponse.ok("Deleted", null));
    }
}
