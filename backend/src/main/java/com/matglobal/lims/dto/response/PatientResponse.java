package com.matglobal.lims.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data @Builder
public class PatientResponse {
    Long id;
    String salutation;
    String name;
    String gender;
    Integer age;
    String ageUnit;
    LocalDate dateOfBirth;
    String mobile;
    String alternateMobile;
    String email;
    String address;
    String remarks;
    String passportNo;
    LocalDateTime createdAt;
    List<RegistrationSummary> registrations;

    @Data @Builder
    public static class RegistrationSummary {
        Long id;
        String regNo;
        String status;
        BigDecimal totalAmount;
        LocalDateTime createdAt;
    }
}
