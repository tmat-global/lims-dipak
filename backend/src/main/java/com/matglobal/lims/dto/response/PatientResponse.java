package com.matglobal.lims.dto.response;

import lombok.*;
import java.time.*;
import java.util.List;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {

    private Long id;
    private String salutation;
    private String name;
    private String gender;
    private Integer age;
    private String ageUnit;
    private String mobile;
    private String alternateMobile;
    private String email;
    private String address;
    private String remarks;
    private String passportNo;

    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;

    private List<RegistrationSummary> registrations;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegistrationSummary {
        private Long id;
        private String regNo;
        private String status;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;
    }
}