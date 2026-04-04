package com.matglobal.lims.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public class AuthRequests {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class RegisterUserRequest {

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 80)
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @Pattern(regexp = "^[0-9+\\-\\s]*$", message = "Invalid mobile number")
        private String mobile;

        @NotBlank(message = "Role is required")
        private String role; // ADMIN, DOCTOR, LAB_TECHNICIAN, RECEPTIONIST
    }

    @Data
    public static class ChangePasswordRequest {

        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String newPassword;
    }
}