package com.matglobal.lims.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data @Builder
public class TestResponse {
    Long id; String code; String name; String type; String department;
    BigDecimal rate; String description; String sampleType;
    Integer turnaroundHours; Boolean isActive; LocalDateTime createdAt;
}
