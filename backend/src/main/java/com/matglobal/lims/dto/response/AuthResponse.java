package com.matglobal.lims.dto.response;
import java.util.Set;

public class AuthResponse {
    private String token, type, username, fullName, email;
    private Long id;
    private Set<String> roles;

    public String getToken() { return token; } public void setToken(String t) { token = t; }
    public String getType() { return type; } public void setType(String t) { type = t; }
    public String getUsername() { return username; } public void setUsername(String u) { username = u; }
    public String getFullName() { return fullName; } public void setFullName(String f) { fullName = f; }
    public String getEmail() { return email; } public void setEmail(String e) { email = e; }
    public Long getId() { return id; } public void setId(Long i) { id = i; }
    public Set<String> getRoles() { return roles; } public void setRoles(Set<String> r) { roles = r; }
}
