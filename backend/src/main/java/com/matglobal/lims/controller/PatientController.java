package com.matglobal.lims.controller;

import com.matglobal.lims.dto.request.PatientRequest;
import com.matglobal.lims.dto.response.*;
import com.matglobal.lims.service.impl.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody PatientRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Patient created", patientService.create(req)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.findById(id)));
    }

    @GetMapping("/by-mobile/{mobile}")
    public ResponseEntity<ApiResponse<PatientResponse>> getByMobile(@PathVariable String mobile) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.findByMobile(mobile)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PatientResponse>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobile,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.ok(
                patientService.search(name, mobile, page, size)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest req) {

        return ResponseEntity.ok(ApiResponse.ok("Patient updated",
                patientService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Patient deleted", null));
    }
}