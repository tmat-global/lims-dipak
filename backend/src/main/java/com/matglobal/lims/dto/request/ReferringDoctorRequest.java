package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReferringDoctorRequest {
    String code; // auto-generated if blank
    @NotBlank(message = "Doctor name is required") String name;
    String mobile;
    @Email String email;
    String address;
    String city;
    String patientType;
    String rateType;
}
