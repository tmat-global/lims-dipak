package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TestRequest {

    @NotBlank(message = "Test code is required")
    private String code;

    @NotBlank(message = "Test name is required")
    private String name;

    private String type;
    private String department;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0")
    private BigDecimal rate;

    private String description;
    private String sampleType;

    @Min(value = 0, message = "Turnaround time cannot be negative")
    private Integer turnaroundHours;

    private Boolean isActive = true;
}