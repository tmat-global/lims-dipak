package com.matglobal.lims.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RegistrationRequest {

    @NotNull(message = "Patient ID is required")
    Long patientId;

    Long refDoctorId;

    @NotBlank String patientType; // OPD, IPD, Corporate
    String center;
    String paymentType;

    BigDecimal otherCharges;
    BigDecimal discountAmount;
    String discountType; // Amt, Per%
    BigDecimal paidAmount;
    String remarks;

    Boolean notifyOnLab;
    Boolean notifyEmail;
    Boolean notifyWhatsapp;
    Boolean isEmergency;

    @NotEmpty(message = "At least one test is required")
    List<Long> testIds;

    @Data
    public static class RegistrationTestEntry {
        @NotNull Long testId;
        BigDecimal clientRate; // optional override
    }
}
