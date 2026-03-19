package com.matglobal.lims.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestResponse {
    private Long id;
    private String code, name, type, department, description, sampleType;
    private BigDecimal rate;
    private Integer turnaroundHours;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public String getCode() { return code; } public void setCode(String v) { code = v; }
    public String getName() { return name; } public void setName(String v) { name = v; }
    public String getType() { return type; } public void setType(String v) { type = v; }
    public String getDepartment() { return department; } public void setDepartment(String v) { department = v; }
    public String getDescription() { return description; } public void setDescription(String v) { description = v; }
    public String getSampleType() { return sampleType; } public void setSampleType(String v) { sampleType = v; }
    public BigDecimal getRate() { return rate; } public void setRate(BigDecimal v) { rate = v; }
    public Integer getTurnaroundHours() { return turnaroundHours; } public void setTurnaroundHours(Integer v) { turnaroundHours = v; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean v) { isActive = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
