package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.response.DashboardResponse;
import com.matglobal.lims.entity.Registration;
import com.matglobal.lims.entity.RegistrationTest;
import com.matglobal.lims.repository.PatientRepository;
import com.matglobal.lims.repository.RegistrationRepository;
import com.matglobal.lims.repository.RegistrationTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RegistrationRepository registrationRepository;
    private final PatientRepository      patientRepository;
    private final RegistrationTestRepository registrationTestRepository;

    /* ── Existing stats endpoint ── */
    @Transactional(readOnly = true)
    public DashboardResponse getStats() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);

        long todayReg         = registrationRepository.countTodayRegistrations(startOfDay);
        BigDecimal todayCol   = registrationRepository.sumTodayCollection(startOfDay);
        long totalPatients    = patientRepository.count();
        long pendingSamples   = registrationRepository.countTodayByStatus(Registration.RegistrationStatus.REGISTERED, startOfDay)
                              + registrationRepository.countTodayByStatus(Registration.RegistrationStatus.SAMPLE_COLLECTED, startOfDay);
        long completedTests   = registrationTestRepository.countByStatus(RegistrationTest.TestStatus.TESTED);
        long pendingReports   = registrationTestRepository.countByStatus(RegistrationTest.TestStatus.PENDING);
        long authorizedReports= registrationTestRepository.countByStatus(RegistrationTest.TestStatus.AUTHORIZED);
        long dispatched       = registrationRepository.countByStatus(Registration.RegistrationStatus.COMPLETED);

        DashboardResponse r = new DashboardResponse();
        r.setTodayRegistrations(todayReg);
        r.setPendingSamples(pendingSamples);
        r.setCompletedTests(completedTests);
        r.setPendingReports(pendingReports);
        r.setTodayCollection(todayCol != null ? todayCol : BigDecimal.ZERO);
        r.setAuthorizedReports(authorizedReports);
        r.setTotalPatients(totalPatients);
        r.setDispatched(dispatched);
        return r;
    }

    /* ── NEW: daily patient counts for area chart ── */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailyPatients(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.atTime(LocalTime.MAX);

        // Fetch all regs in range and group by date
        List<Registration> regs = registrationRepository.findByDateRange(fromDt, toDt);

        // Build a map date -> count
        Map<LocalDate, Long> countMap = new LinkedHashMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) countMap.put(d, 0L);
        regs.forEach(r -> {
            LocalDate d = r.getCreatedAt().toLocalDate();
            countMap.computeIfPresent(d, (k, v) -> v + 1);
        });

        List<Map<String, Object>> result = new ArrayList<>();
        countMap.forEach((date, count) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date",  date.toString());
            row.put("count", count);
            result.add(row);
        });
        return result;
    }

    /* ── NEW: patients by department for bar chart ── */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPatientsByDepartment(int month, int year) {
        YearMonth ym  = YearMonth.of(year, month);
        LocalDateTime from = ym.atDay(1).atStartOfDay();
        LocalDateTime to   = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<Object[]> rows = registrationRepository.countByDepartment(from, to);
        List<Map<String, Object>> result = new ArrayList<>();
        rows.forEach(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("department", row[0]);
            m.put("count",      row[1]);
            result.add(m);
        });
        return result;
    }

    /* ── NEW: top referral doctors for pie chart ── */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getReferralDoctors(int month, int year) {
        YearMonth ym  = YearMonth.of(year, month);
        LocalDateTime from = ym.atDay(1).atStartOfDay();
        LocalDateTime to   = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<Object[]> rows = registrationRepository.countByReferringDoctor(from, to);
        List<Map<String, Object>> result = new ArrayList<>();
        rows.forEach(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("doctor", row[0]);
            m.put("count",  row[1]);
            result.add(m);
        });
        return result;
    }
}
