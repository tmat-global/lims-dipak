package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PatientRequest {
    String salutation;
    @NotBlank(message = "Name is required") @Size(max=150) String name;
    @NotBlank String gender;
    Integer age;
    String ageUnit;
    LocalDate dateOfBirth;
    @Pattern(regexp="^[0-9+\\-\\s]*$", message="Invalid mobile") String mobile;
    String alternateMobile;
    @Email String email;
    String address;
    String remarks;
    String passportNo;
}
