package com.matglobal.lims.controller;

import com.matglobal.lims.service.impl.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/patient/{registrationId}")
    public ResponseEntity<?> getPatientReport(@PathVariable Long registrationId) {
        try {
            byte[] pdf = reportService.generatePatientReport(registrationId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=report_" + registrationId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            log.error("Report generation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage() + " | Cause: " + 
                          (e.getCause() != null ? e.getCause().getMessage() : "unknown"));
        }
    }
}
