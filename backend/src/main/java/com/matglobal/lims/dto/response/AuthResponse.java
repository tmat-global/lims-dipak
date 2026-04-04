package com.matglobal.lims.dto.response;

import lombok.*;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private String fullName;
    private String email;
    private Long id;
    private Set<String> roles;
}