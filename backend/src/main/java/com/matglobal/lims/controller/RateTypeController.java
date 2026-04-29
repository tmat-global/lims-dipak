package com.matglobal.lims.controller;

import com.matglobal.lims.dto.response.ApiResponse;
import com.matglobal.lims.entity.RateType;
import com.matglobal.lims.repository.RateTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/rate-types")
@RequiredArgsConstructor
public class RateTypeController {

    private final RateTypeRepository rateTypeRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> findAll(
            @RequestParam(required = false) String masterType) {
        List<RateType> list = masterType != null
            ? rateTypeRepository.findByMasterTypeAndIsActiveTrueOrderByCreatedAtDesc(masterType)
            : rateTypeRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        List<Map<String, Object>> result = list.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("name", r.getName());
            m.put("masterType", r.getMasterType());
            m.put("rateType", r.getRateType());
            m.put("createdAt", r.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @RequestBody Map<String, String> body) {
        String name       = body.getOrDefault("name", "").trim();
        String masterType = body.getOrDefault("masterType", "CENTER");
        String rateType   = body.getOrDefault("rateType", "General");
        if (name.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Rate Type Name is required"));
        }
        if (rateTypeRepository.existsByNameAndMasterTypeAndIsActiveTrue(name, masterType)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Rate Type already exists: " + name));
        }
        RateType rt = RateType.builder()
            .name(name).masterType(masterType).rateType(rateType).isActive(true).build();
        rateTypeRepository.save(rt);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", rt.getId()); m.put("name", rt.getName());
        m.put("masterType", rt.getMasterType()); m.put("rateType", rt.getRateType());
        return ResponseEntity.ok(ApiResponse.ok("Rate Type created", m));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        RateType rt = rateTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Not found"));
        if (body.containsKey("name"))       rt.setName(body.get("name").trim());
        if (body.containsKey("masterType")) rt.setMasterType(body.get("masterType"));
        if (body.containsKey("rateType"))   rt.setRateType(body.get("rateType"));
        rateTypeRepository.save(rt);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", rt.getId()); m.put("name", rt.getName());
        m.put("masterType", rt.getMasterType()); m.put("rateType", rt.getRateType());
        return ResponseEntity.ok(ApiResponse.ok("Rate Type updated", m));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        RateType rt = rateTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Not found"));
        rt.setIsActive(false);
        rateTypeRepository.save(rt);
        return ResponseEntity.ok(ApiResponse.ok("Deleted", null));
    }
}
