package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// ── AUTH ──────────────────────────────────────────────────────────────
public class AuthRequests {

    @Data
    public static class LoginRequest {
        @NotBlank String username;
        @NotBlank String password;
    }

    @Data
    public static class RegisterUserRequest {
        @NotBlank @Size(min=3,max=80) String username;
        @NotBlank @Size(min=6) String password;
        @NotBlank String firstName;
        @NotBlank String lastName;
        @NotBlank @Email String email;
        String mobile;
        @NotBlank String role; // ADMIN, DOCTOR, LAB_TECHNICIAN, RECEPTIONIST
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank String currentPassword;
        @NotBlank @Size(min=6) String newPassword;
    }
}
