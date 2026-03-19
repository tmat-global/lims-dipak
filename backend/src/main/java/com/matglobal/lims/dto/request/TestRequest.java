package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TestRequest {
    @NotBlank String code;
    @NotBlank String name;
    String type;
    String department;
    @NotNull BigDecimal rate;
    String description;
    String sampleType;
    Integer turnaroundHours;
    Boolean isActive;
}
