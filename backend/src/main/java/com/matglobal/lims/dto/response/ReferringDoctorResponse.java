package com.matglobal.lims.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data @Builder
public class ReferringDoctorResponse {
    Long id; String code; String name; String mobile; String email;
    String address; String city; String patientType; String rateType;
    Boolean isActive; LocalDateTime createdAt;
}
