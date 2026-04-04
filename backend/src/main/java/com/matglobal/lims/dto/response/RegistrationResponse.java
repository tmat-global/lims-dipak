package com.matglobal.lims.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {

    private Long id;
    private String regNo;
    private String patientType;
    private String center;
    private String paymentType;
    private String discountType;
    private String remarks;
    private String status;
    private String createdBy;

    private BigDecimal totalAmount;
    private BigDecimal otherCharges;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;

    private Boolean notifyOnLab;
    private Boolean notifyEmail;
    private Boolean notifyWhatsapp;
    private Boolean isEmergency;

    private LocalDateTime createdAt;

    private PatientInfo patient;
    private RefDoctorInfo refDoctor;
    private List<TestInfo> tests;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatientInfo {
        private Long id;
        private String name;
        private String gender;
        private Integer age;
        private String ageUnit;
        private String mobile;
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefDoctorInfo {
        private Long id;
        private String code;
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestInfo {
        private Long id;
        private String code;
        private String name;
        private String type;
        private String status;
        private BigDecimal rate;
        private BigDecimal clientRate;
    }
}