package com.matglobal.lims.controller;

import com.matglobal.lims.dto.request.*;
import com.matglobal.lims.dto.response.*;
import com.matglobal.lims.service.impl.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// ════════════════════════════════════════════════════════
//  AUTH CONTROLLER
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequests.LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(req)));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody AuthRequests.RegisterUserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered", authService.registerUser(req)));
    }
}

// ════════════════════════════════════════════════════════
//  PATIENT CONTROLLER
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
class PatientController {
    private final PatientService patientService;

    @PostMapping
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
        return ResponseEntity.ok(ApiResponse.ok(patientService.search(name, mobile, page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody PatientRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Patient updated", patientService.update(id, req)));
    }
}

// ════════════════════════════════════════════════════════
//  REGISTRATION CONTROLLER
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<ApiResponse<RegistrationResponse>> create(@Valid @RequestBody RegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration saved", registrationService.create(req)));
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
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", registrationService.updateStatus(id, status)));
    }
}

// ════════════════════════════════════════════════════════
//  TEST CONTROLLER
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
class TestController {
    private final TestService testService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TestResponse>> create(@Valid @RequestBody TestRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Test created", testService.create(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TestResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(testService.findAll()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TestResponse>>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok(testService.search(q)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TestResponse>> update(@PathVariable Long id,
                                                            @Valid @RequestBody TestRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Test updated", testService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        testService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Test deactivated", null));
    }
}

// ════════════════════════════════════════════════════════
//  REFERRING DOCTOR CONTROLLER
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/ref-doctors")
@RequiredArgsConstructor
class ReferringDoctorController {
    private final ReferringDoctorService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ReferringDoctorResponse>> create(@Valid @RequestBody ReferringDoctorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Doctor added", service.create(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReferringDoctorResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.findAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReferringDoctorResponse>> update(@PathVariable Long id,
                                                                        @Valid @RequestBody ReferringDoctorRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Doctor updated", service.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Doctor removed", null));
    }
}

// ════════════════════════════════════════════════════════
//  USER CONTROLLER
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
class UserController {
    private final UserManagementService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(userService.findAll(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id,
                                                            @Valid @RequestBody AuthRequests.RegisterUserRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("User updated", userService.update(id, req)));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        userService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled", null));
    }
}

// ════════════════════════════════════════════════════════
//  DASHBOARD CONTROLLER
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getStats()));
    }
}
