package com.matglobal.lims.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class RegistrationResponse {
    Long id;
    String regNo;
    PatientInfo patient;
    RefDoctorInfo refDoctor;
    String patientType;
    String center;
    String paymentType;
    BigDecimal totalAmount;
    BigDecimal otherCharges;
    BigDecimal discountAmount;
    String discountType;
    BigDecimal netAmount;
    BigDecimal paidAmount;
    BigDecimal balanceAmount;
    String remarks;
    Boolean notifyOnLab;
    Boolean notifyEmail;
    Boolean notifyWhatsapp;
    Boolean isEmergency;
    String status;
    List<TestInfo> tests;
    LocalDateTime createdAt;
    String createdBy;

    @Data @Builder
    public static class PatientInfo {
        Long id; String name; String gender; Integer age; String ageUnit; String mobile; String email;
    }

    @Data @Builder
    public static class RefDoctorInfo {
        Long id; String code; String name;
    }

    @Data @Builder
    public static class TestInfo {
        Long id; String code; String name; String type; BigDecimal rate; BigDecimal clientRate; String status;
    }
}
