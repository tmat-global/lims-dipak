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
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final RegistrationRepository registrationRepository;
    private final PatientRepository patientRepository;
    private final RegistrationTestRepository registrationTestRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getStats() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);

        long todayReg = registrationRepository.countTodayRegistrations(startOfDay);
        BigDecimal todayCollection = registrationRepository.sumTodayCollection(startOfDay);
        long totalPatients = patientRepository.count();

        // Pending samples = registrations in REGISTERED or SAMPLE_COLLECTED status (today)
        long pendingSamples = registrationRepository.countTodayByStatus(
                Registration.RegistrationStatus.REGISTERED, startOfDay)
                + registrationRepository.countTodayByStatus(
                Registration.RegistrationStatus.SAMPLE_COLLECTED, startOfDay);

        // Completed tests = registration tests with TESTED status
        long completedTests = registrationTestRepository.countByStatus(RegistrationTest.TestStatus.TESTED);

        // Pending reports = registration tests with PENDING status
        long pendingReports = registrationTestRepository.countByStatus(RegistrationTest.TestStatus.PENDING);

        // Authorized reports = registration tests with AUTHORIZED status
        long authorizedReports = registrationTestRepository.countByStatus(RegistrationTest.TestStatus.AUTHORIZED);

        // Dispatched = registrations in COMPLETED status
        long dispatched = registrationRepository.countByStatus(Registration.RegistrationStatus.COMPLETED);

        DashboardResponse r = new DashboardResponse();
        r.setTodayRegistrations(todayReg);
        r.setPendingSamples(pendingSamples);
        r.setCompletedTests(completedTests);
        r.setPendingReports(pendingReports);
        r.setTodayCollection(todayCollection != null ? todayCollection : BigDecimal.ZERO);
        r.setAuthorizedReports(authorizedReports);
        r.setTotalPatients(totalPatients);
        r.setDispatched(dispatched);
        return r;
    }
}
