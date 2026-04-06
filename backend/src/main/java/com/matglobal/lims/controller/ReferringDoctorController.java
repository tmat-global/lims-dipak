package com.matglobal.lims.controller;

import com.matglobal.lims.dto.request.ReferringDoctorRequest;
import com.matglobal.lims.dto.response.*;
import com.matglobal.lims.service.impl.ReferringDoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ref-doctors")
@RequiredArgsConstructor
public class ReferringDoctorController {

    private final ReferringDoctorService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ReferringDoctorResponse>> create(
            @Valid @RequestBody ReferringDoctorRequest req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Doctor added", service.create(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReferringDoctorResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.findAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReferringDoctorResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ReferringDoctorRequest req) {

        return ResponseEntity.ok(ApiResponse.ok("Doctor updated",
                service.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Doctor deleted", null));
    }
}