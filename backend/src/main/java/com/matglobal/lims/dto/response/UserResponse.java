package com.matglobal.lims.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data @Builder
public class UserResponse {
    Long id; String username; String firstName; String lastName;
    String email; String mobile; Boolean isActive; Set<String> roles;
    LocalDateTime createdAt;
}
