package com.matglobal.lims.controller;

import com.matglobal.lims.dto.request.TestRequest;
import com.matglobal.lims.dto.response.*;
import com.matglobal.lims.service.impl.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tests")
@RequiredArgsConstructor
public class TestController {

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
    public ResponseEntity<ApiResponse<TestResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TestRequest req) {

        return ResponseEntity.ok(ApiResponse.ok("Test updated",
                testService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        testService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Test deleted", null));
    }
}