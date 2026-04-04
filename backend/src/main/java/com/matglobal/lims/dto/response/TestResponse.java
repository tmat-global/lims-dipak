package com.matglobal.lims.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestResponse {

    private Long id;
    private String code;
    private String name;
    private String type;
    private String department;
    private String description;
    private String sampleType;

    private BigDecimal rate;
    private Integer turnaroundHours;

    private Boolean isActive;
    private LocalDateTime createdAt;
}