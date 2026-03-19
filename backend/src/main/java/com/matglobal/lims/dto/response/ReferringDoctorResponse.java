package com.matglobal.lims.dto.response;
import java.time.LocalDateTime;

public class ReferringDoctorResponse {
    private Long id;
    private String code, name, mobile, email, address, city, patientType, rateType;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public String getCode() { return code; } public void setCode(String v) { code = v; }
    public String getName() { return name; } public void setName(String v) { name = v; }
    public String getMobile() { return mobile; } public void setMobile(String v) { mobile = v; }
    public String getEmail() { return email; } public void setEmail(String v) { email = v; }
    public String getAddress() { return address; } public void setAddress(String v) { address = v; }
    public String getCity() { return city; } public void setCity(String v) { city = v; }
    public String getPatientType() { return patientType; } public void setPatientType(String v) { patientType = v; }
    public String getRateType() { return rateType; } public void setRateType(String v) { rateType = v; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean v) { isActive = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
