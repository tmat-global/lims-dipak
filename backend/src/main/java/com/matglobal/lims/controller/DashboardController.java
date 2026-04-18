package com.matglobal.lims.controller;

import com.matglobal.lims.dto.response.ApiResponse;
import com.matglobal.lims.dto.response.DashboardResponse;
import com.matglobal.lims.service.impl.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /* ── Existing stats ── */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getStats()));
    }

    /* ── NEW: daily patients for area chart
       GET /api/v1/dashboard/daily-patients?from=2026-03-19&to=2026-04-18 ── */
    @GetMapping("/daily-patients")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDailyPatients(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(29)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDailyPatients(from, to)));
    }

    /* ── NEW: patients by department for bar chart
       GET /api/v1/dashboard/patients-by-department?month=4&year=2026 ── */
    @GetMapping("/patients-by-department")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPatientsByDepartment(
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {
        LocalDate now = LocalDate.now();
        int m = month == 0 ? now.getMonthValue() : month;
        int y = year  == 0 ? now.getYear()       : year;
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getPatientsByDepartment(m, y)));
    }

    /* ── NEW: referral doctors for pie chart
       GET /api/v1/dashboard/referral-doctors?month=4&year=2026 ── */
    @GetMapping("/referral-doctors")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReferralDoctors(
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {
        LocalDate now = LocalDate.now();
        int m = month == 0 ? now.getMonthValue() : month;
        int y = year  == 0 ? now.getYear()       : year;
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getReferralDoctors(m, y)));
    }
}
