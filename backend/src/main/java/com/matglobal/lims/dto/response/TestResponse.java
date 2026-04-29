package com.matglobal.lims.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TestResponse {
    private Long id;
    private String code;
    private String name;
    private String type;
    private String department;
    private BigDecimal rate;
    private String description;
    private String sampleType;
    private Integer turnaroundHours;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String parameters;
    private String ranges;
    private String formula;
    private String reportNotes;
    private String testConfig;
}
