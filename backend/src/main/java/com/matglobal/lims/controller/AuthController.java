package com.matglobal.lims.controller;

import com.matglobal.lims.dto.request.AuthRequests;
import com.matglobal.lims.dto.response.*;
import com.matglobal.lims.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth") // ✅ FIXED: removed /api/v1 — context-path /api is already set in
                         // application.properties
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequests.LoginRequest req) {

        return ResponseEntity.ok(
                ApiResponse.ok("Login successful", authService.login(req)));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody AuthRequests.RegisterUserRequest req) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered", authService.registerUser(req)));
    }
}