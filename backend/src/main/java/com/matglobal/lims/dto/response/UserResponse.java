package com.matglobal.lims.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;

    private Boolean isActive;
    private Set<String> roles;

    private LocalDateTime createdAt;
}