package com.matglobal.lims.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class DashboardResponse {
    long todayRegistrations;
    long pendingSamples;
    long completedTests;
    long pendingReports;
    BigDecimal todayCollection;
    long authorizedReports;
    long totalPatients;
    long dispatched;
}
