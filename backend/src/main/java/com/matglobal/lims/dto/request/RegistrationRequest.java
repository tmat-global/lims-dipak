package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RegistrationRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long refDoctorId;

    @NotBlank(message = "Patient type is required")
    private String patientType; // OPD, IPD, Corporate

    private String center;
    private String paymentType;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal otherCharges;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discountAmount;

    private String discountType; // Amt, Per%

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal paidAmount;

    private String remarks;

    private Boolean notifyOnLab = false;
    private Boolean notifyEmail = false;
    private Boolean notifyWhatsapp = false;
    private Boolean isEmergency = false;

    @NotEmpty(message = "At least one test is required")
    private List<Long> testIds;
}