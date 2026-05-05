package com.matglobal.lims.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TestRequest {
    @NotBlank(message = "Test code is required")
    private String code;

    @NotBlank(message = "Test name is required")
    private String name;

    private String type;
    private String department;
    private BigDecimal rate;
    private String description;
    private String sampleType;
    private Integer turnaroundHours;
    private Boolean isActive = true;
    private String parameters;
    private String ranges;
    private String formula;
    private String reportNotes;
    private String testConfig;
    private List<Long> packageTests;
}
