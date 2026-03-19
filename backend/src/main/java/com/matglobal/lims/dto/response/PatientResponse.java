package com.matglobal.lims.dto.response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PatientResponse {
    private Long id;
    private String salutation, name, gender, ageUnit, mobile, alternateMobile, email, address, remarks, passportNo;
    private Integer age;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
    private List<RegistrationSummary> registrations;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public String getSalutation() { return salutation; } public void setSalutation(String v) { salutation = v; }
    public String getName() { return name; } public void setName(String v) { name = v; }
    public String getGender() { return gender; } public void setGender(String v) { gender = v; }
    public String getAgeUnit() { return ageUnit; } public void setAgeUnit(String v) { ageUnit = v; }
    public String getMobile() { return mobile; } public void setMobile(String v) { mobile = v; }
    public String getAlternateMobile() { return alternateMobile; } public void setAlternateMobile(String v) { alternateMobile = v; }
    public String getEmail() { return email; } public void setEmail(String v) { email = v; }
    public String getAddress() { return address; } public void setAddress(String v) { address = v; }
    public String getRemarks() { return remarks; } public void setRemarks(String v) { remarks = v; }
    public String getPassportNo() { return passportNo; } public void setPassportNo(String v) { passportNo = v; }
    public Integer getAge() { return age; } public void setAge(Integer v) { age = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; } public void setDateOfBirth(LocalDate v) { dateOfBirth = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
    public List<RegistrationSummary> getRegistrations() { return registrations; } public void setRegistrations(List<RegistrationSummary> v) { registrations = v; }

    public static class RegistrationSummary {
        private Long id; private String regNo, status; private BigDecimal totalAmount; private LocalDateTime createdAt;
        public Long getId() { return id; } public void setId(Long v) { id = v; }
        public String getRegNo() { return regNo; } public void setRegNo(String v) { regNo = v; }
        public String getStatus() { return status; } public void setStatus(String v) { status = v; }
        public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { totalAmount = v; }
        public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
    }
}
