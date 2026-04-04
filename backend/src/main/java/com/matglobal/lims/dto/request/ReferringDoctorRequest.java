package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReferringDoctorRequest {

    private String code; // auto-generated if blank

    @NotBlank(message = "Doctor name is required")
    private String name;

    @Pattern(regexp = "^[0-9+\\-\\s]*$", message = "Invalid mobile number")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
    private String city;
    private String patientType;
    private String rateType;
}