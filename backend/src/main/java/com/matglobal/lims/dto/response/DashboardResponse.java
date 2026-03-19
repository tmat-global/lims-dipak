package com.matglobal.lims.dto.response;
import java.math.BigDecimal;

public class DashboardResponse {
    private long todayRegistrations, pendingSamples, completedTests, pendingReports, authorizedReports, totalPatients, dispatched;
    private BigDecimal todayCollection;

    public long getTodayRegistrations() { return todayRegistrations; } public void setTodayRegistrations(long v) { todayRegistrations = v; }
    public long getPendingSamples() { return pendingSamples; } public void setPendingSamples(long v) { pendingSamples = v; }
    public long getCompletedTests() { return completedTests; } public void setCompletedTests(long v) { completedTests = v; }
    public long getPendingReports() { return pendingReports; } public void setPendingReports(long v) { pendingReports = v; }
    public long getAuthorizedReports() { return authorizedReports; } public void setAuthorizedReports(long v) { authorizedReports = v; }
    public long getTotalPatients() { return totalPatients; } public void setTotalPatients(long v) { totalPatients = v; }
    public long getDispatched() { return dispatched; } public void setDispatched(long v) { dispatched = v; }
    public BigDecimal getTodayCollection() { return todayCollection; } public void setTodayCollection(BigDecimal v) { todayCollection = v; }
}
