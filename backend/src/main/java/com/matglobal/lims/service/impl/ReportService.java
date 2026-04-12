package com.matglobal.lims.service.impl;

import com.matglobal.lims.entity.Registration;
import com.matglobal.lims.entity.RegistrationTest;
import com.matglobal.lims.exception.ResourceNotFoundException;
import com.matglobal.lims.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RegistrationRepository registrationRepository;

    @Transactional(readOnly = true)
    public byte[] generatePatientReport(Long registrationId) throws Exception {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", registrationId));

        // Eagerly access all lazy fields within transaction
        String patientName = reg.getPatient().getName();
        String age = reg.getPatient().getAge() + " " + reg.getPatient().getAgeUnit();
        String gender = reg.getPatient().getGender();
        String mobile = reg.getPatient().getMobile() != null ? reg.getPatient().getMobile() : "";
        String refDoctor = reg.getRefDoctor() != null ? reg.getRefDoctor().getName() : "Self";
        String center = reg.getCenter() != null ? reg.getCenter() : "";
        String regNo = reg.getRegNo();
        String reportDate = reg.getCreatedAt() != null
                ? reg.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                : new Date().toString();

        // Build data rows
        List<Map<String, String>> rows = new ArrayList<>();
        for (RegistrationTest rt : reg.getRegistrationTests()) {
            Map<String, String> row = new HashMap<>();
            row.put("testName", rt.getTest().getName());
            row.put("result", rt.getResultValue() != null ? rt.getResultValue() : "Pending");
            row.put("unit", rt.getResultUnit() != null ? rt.getResultUnit() : "");
            row.put("referenceRange", rt.getReferenceRange() != null ? rt.getReferenceRange() : "");
            row.put("status", rt.getStatus().name());
            rows.add(row);
        }

        // Build parameters map
        Map<String, Object> params = new HashMap<>();
        params.put("patientName", patientName);
        params.put("regNo", regNo);
        params.put("age", age);
        params.put("gender", gender);
        params.put("mobile", mobile);
        params.put("refDoctor", refDoctor);
        params.put("center", center);
        params.put("reportDate", reportDate);

        // Load and compile template
        InputStream templateStream = new ClassPathResource("reports/patient_report.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

        // Fill report
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(rows);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
