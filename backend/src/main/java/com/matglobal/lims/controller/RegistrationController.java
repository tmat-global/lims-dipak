package com.matglobal.lims.controller;

import com.matglobal.lims.dto.request.RegistrationRequest;
import com.matglobal.lims.dto.response.*;
import com.matglobal.lims.service.impl.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<ApiResponse<RegistrationResponse>> create(
            @Valid @RequestBody RegistrationRequest req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration saved",
                        registrationService.create(req)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistrationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(registrationService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RegistrationResponse>>> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String regNo,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.ok(
                registrationService.search(from, to, patientName, mobile, regNo, status, page, size)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RegistrationResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(ApiResponse.ok("Status updated",
                registrationService.updateStatus(id, status)));
    }
}