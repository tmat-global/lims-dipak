package com.matglobal.lims.dto.response;

import com.matglobal.lims.entity.Registration;
import com.matglobal.lims.entity.RegistrationTest;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// ── Auth ──────────────────────────────────────────────────────────────
@Data @Builder
public class AuthResponse {
    String token;
    String type;
    Long id;
    String username;
    String fullName;
    String email;
    Set<String> roles;
}
