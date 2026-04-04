package com.matglobal.lims.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {

    private long todayRegistrations;
    private long pendingSamples;
    private long completedTests;
    private long pendingReports;
    private long authorizedReports;
    private long totalPatients;
    private long dispatched;

    private BigDecimal todayCollection;
}