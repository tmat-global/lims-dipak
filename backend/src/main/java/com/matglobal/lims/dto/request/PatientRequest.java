package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequest {

    private String salutation;

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name cannot exceed 150 characters")
    private String name;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Min(value = 0, message = "Age cannot be negative")
    private Integer age;

    private String ageUnit; // Years, Months, Days

    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[0-9+\\-\\s]*$", message = "Invalid mobile number")
    private String mobile;

    @Pattern(regexp = "^[0-9+\\-\\s]*$", message = "Invalid alternate mobile")
    private String alternateMobile;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
    private String remarks;
    private String passportNo;
}