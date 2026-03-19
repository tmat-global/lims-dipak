package com.matglobal.lims.dto.response;
import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse {
    private Long id;
    private String username, firstName, lastName, email, mobile;
    private Boolean isActive;
    private Set<String> roles;
    private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public String getUsername() { return username; } public void setUsername(String v) { username = v; }
    public String getFirstName() { return firstName; } public void setFirstName(String v) { firstName = v; }
    public String getLastName() { return lastName; } public void setLastName(String v) { lastName = v; }
    public String getEmail() { return email; } public void setEmail(String v) { email = v; }
    public String getMobile() { return mobile; } public void setMobile(String v) { mobile = v; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean v) { isActive = v; }
    public Set<String> getRoles() { return roles; } public void setRoles(Set<String> v) { roles = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
