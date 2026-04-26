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
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
    private static final DateTimeFormatter DF  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional(readOnly = true)
    public byte[] generatePatientReport(Long registrationId) throws Exception {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", registrationId));

        // Eagerly load all fields
        String patientName    = reg.getPatient().getName() != null ? reg.getPatient().getName() : "";
        String patientAddress = reg.getPatient().getAddress() != null ? reg.getPatient().getAddress() : "";
        String age            = reg.getPatient().getAge() + " " + reg.getPatient().getAgeUnit();
        String gender         = reg.getPatient().getGender() != null ? reg.getPatient().getGender() : "";
        String mobile         = reg.getPatient().getMobile() != null ? reg.getPatient().getMobile() : "";
        String dob            = reg.getPatient().getDateOfBirth() != null
                ? reg.getPatient().getDateOfBirth().format(DF) : "";
        String refDoctor      = reg.getRefDoctor() != null ? reg.getRefDoctor().getName() : "Self Requested";
        String center         = reg.getCenter() != null ? reg.getCenter() : "OPD";
        String regNo          = reg.getRegNo();
        String billedDT       = reg.getCreatedAt() != null ? reg.getCreatedAt().format(DTF) : "";
        String reportDT       = reg.getUpdatedAt() != null ? reg.getUpdatedAt().format(DTF) : billedDT;

        // Parameters
        Map<String, Object> params = new HashMap<>();
        params.put("patientId",       String.valueOf(reg.getPatient().getId()));
        params.put("medLabId",        regNo);
        params.put("patientName",     patientName);
        params.put("patientAddress",  patientAddress);
        params.put("age",             age);
        params.put("dob",             dob);
        params.put("gender",          gender);
        params.put("contactNo",       mobile);
        params.put("requestedBy",     refDoctor);
        params.put("billedDateTime",  billedDT);
        params.put("sampleCollected", billedDT);
        params.put("reportCompleted", reportDT);
        params.put("primarySample",   "Blood");
        params.put("specimenRemark",  "Accepted");
        params.put("center",          center);
        params.put("regNo",           regNo);
        params.put("technicianName",  reg.getCreatedBy() != null ? reg.getCreatedBy() : "Lab Technician");
        params.put("labManagerName",  "Lab Manager");

        // Data rows
        List<Map<String, String>> rows = new ArrayList<>();
        for (RegistrationTest rt : reg.getRegistrationTests()) {
            Map<String, String> row = new HashMap<>();
            row.put("department",     rt.getTest().getDepartment() != null ? rt.getTest().getDepartment() : "General");
            row.put("testName",       rt.getTest().getName());
            row.put("result",         rt.getResultValue() != null ? rt.getResultValue() : "Pending");
            row.put("unit",           rt.getResultUnit() != null ? rt.getResultUnit() : "");
            row.put("referenceRange", rt.getReferenceRange() != null ? rt.getReferenceRange() : "");
            row.put("method",         "");
            row.put("status",         rt.getStatus().name());
            rows.add(row);
        }

        // If no tests, add empty row
        if (rows.isEmpty()) {
            Map<String, String> row = new HashMap<>();
            row.put("department", ""); row.put("testName", "No tests found");
            row.put("result", ""); row.put("unit", "");
            row.put("referenceRange", ""); row.put("method", "");
            row.put("status", "");
            rows.add(row);
        }

        InputStream stream = new ClassPathResource("reports/patient_report.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(stream);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(rows);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, ds);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    @Transactional(readOnly = true)
    public byte[] generateBillingReceipt(Long registrationId) throws Exception {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", registrationId));

        String patientName = reg.getPatient().getName() != null ? reg.getPatient().getName() : "";
        String age         = reg.getPatient().getAge() + " " + reg.getPatient().getAgeUnit();
        String gender      = reg.getPatient().getGender() != null ? reg.getPatient().getGender() : "";
        String mobile      = reg.getPatient().getMobile() != null ? reg.getPatient().getMobile() : "";
        String refDoctor   = reg.getRefDoctor() != null ? reg.getRefDoctor().getName() : "Self";
        String billedDT    = reg.getCreatedAt() != null ? reg.getCreatedAt().format(DTF) : "";

        Map<String, Object> params = new HashMap<>();
        params.put("patientName",    patientName);
        params.put("regNo",          reg.getRegNo());
        params.put("age",            age);
        params.put("gender",         gender);
        params.put("mobile",         mobile);
        params.put("refDoctor",      refDoctor);
        params.put("center",         reg.getCenter() != null ? reg.getCenter() : "OPD");
        params.put("receiptDate",    billedDT);
        params.put("paymentType",    reg.getPaymentType() != null ? reg.getPaymentType() : "Cash");
        params.put("totalAmount",    reg.getTotalAmount() != null ? reg.getTotalAmount().toString() : "0");
        params.put("discountAmount", reg.getDiscountAmount() != null ? reg.getDiscountAmount().toString() : "0");
        params.put("netAmount",      reg.getNetAmount() != null ? reg.getNetAmount().toString() : "0");
        params.put("paidAmount",     reg.getPaidAmount() != null ? reg.getPaidAmount().toString() : "0");
        params.put("balanceAmount",  reg.getBalanceAmount() != null ? reg.getBalanceAmount().toString() : "0");
        params.put("receiptNo",      reg.getRegNo());

        List<Map<String, String>> rows = new ArrayList<>();
        for (RegistrationTest rt : reg.getRegistrationTests()) {
            Map<String, String> row = new HashMap<>();
            row.put("testName", rt.getTest().getName());
            row.put("rate",     rt.getRate() != null ? rt.getRate().toString() : "0");
            rows.add(row);
        }

        if (rows.isEmpty()) {
            Map<String, String> row = new HashMap<>();
            row.put("testName", "No tests"); row.put("rate", "0");
            rows.add(row);
        }

        InputStream stream = new ClassPathResource("reports/billing_receipt.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(stream);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(rows);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, ds);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
