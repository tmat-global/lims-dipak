package com.matglobal.lims.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferringDoctorResponse {

    private Long id;
    private String code;
    private String name;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String patientType;
    private String rateType;

    private Boolean isActive;
    private LocalDateTime createdAt;
}