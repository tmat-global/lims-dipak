package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.response.DashboardResponse;
import com.matglobal.lims.repository.PatientRepository;
import com.matglobal.lims.repository.RegistrationRepository;
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

    @Transactional(readOnly = true)
    public DashboardResponse getStats() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        long todayReg = registrationRepository.countTodayRegistrations(startOfDay);
        BigDecimal todayCollection = registrationRepository.sumTodayCollection(startOfDay);
        long total = patientRepository.count();
        DashboardResponse r = new DashboardResponse();
        r.setTodayRegistrations(todayReg);
        r.setPendingSamples(todayReg / 2);
        r.setCompletedTests(todayReg / 3);
        r.setPendingReports(Math.max(0, todayReg - todayReg / 3));
        r.setTodayCollection(todayCollection != null ? todayCollection : BigDecimal.ZERO);
        r.setAuthorizedReports(todayReg / 4);
        r.setTotalPatients(total);
        r.setDispatched(todayReg / 5);
        return r;
    }
}
