package com.matglobal.lims.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RegistrationResponse {
    private Long id; private String regNo, patientType, center, paymentType, discountType, remarks, status, createdBy;
    private BigDecimal totalAmount, otherCharges, discountAmount, netAmount, paidAmount, balanceAmount;
    private Boolean notifyOnLab, notifyEmail, notifyWhatsapp, isEmergency;
    private LocalDateTime createdAt;
    private PatientInfo patient; private RefDoctorInfo refDoctor; private List<TestInfo> tests;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public String getRegNo() { return regNo; } public void setRegNo(String v) { regNo = v; }
    public String getPatientType() { return patientType; } public void setPatientType(String v) { patientType = v; }
    public String getCenter() { return center; } public void setCenter(String v) { center = v; }
    public String getPaymentType() { return paymentType; } public void setPaymentType(String v) { paymentType = v; }
    public String getDiscountType() { return discountType; } public void setDiscountType(String v) { discountType = v; }
    public String getRemarks() { return remarks; } public void setRemarks(String v) { remarks = v; }
    public String getStatus() { return status; } public void setStatus(String v) { status = v; }
    public String getCreatedBy() { return createdBy; } public void setCreatedBy(String v) { createdBy = v; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { totalAmount = v; }
    public BigDecimal getOtherCharges() { return otherCharges; } public void setOtherCharges(BigDecimal v) { otherCharges = v; }
    public BigDecimal getDiscountAmount() { return discountAmount; } public void setDiscountAmount(BigDecimal v) { discountAmount = v; }
    public BigDecimal getNetAmount() { return netAmount; } public void setNetAmount(BigDecimal v) { netAmount = v; }
    public BigDecimal getPaidAmount() { return paidAmount; } public void setPaidAmount(BigDecimal v) { paidAmount = v; }
    public BigDecimal getBalanceAmount() { return balanceAmount; } public void setBalanceAmount(BigDecimal v) { balanceAmount = v; }
    public Boolean getNotifyOnLab() { return notifyOnLab; } public void setNotifyOnLab(Boolean v) { notifyOnLab = v; }
    public Boolean getNotifyEmail() { return notifyEmail; } public void setNotifyEmail(Boolean v) { notifyEmail = v; }
    public Boolean getNotifyWhatsapp() { return notifyWhatsapp; } public void setNotifyWhatsapp(Boolean v) { notifyWhatsapp = v; }
    public Boolean getIsEmergency() { return isEmergency; } public void setIsEmergency(Boolean v) { isEmergency = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
    public PatientInfo getPatient() { return patient; } public void setPatient(PatientInfo v) { patient = v; }
    public RefDoctorInfo getRefDoctor() { return refDoctor; } public void setRefDoctor(RefDoctorInfo v) { refDoctor = v; }
    public List<TestInfo> getTests() { return tests; } public void setTests(List<TestInfo> v) { tests = v; }

    public static class PatientInfo {
        private Long id; private String name, gender, ageUnit, mobile, email; private Integer age;
        public Long getId(){return id;} public void setId(Long v){id=v;}
        public String getName(){return name;} public void setName(String v){name=v;}
        public String getGender(){return gender;} public void setGender(String v){gender=v;}
        public String getAgeUnit(){return ageUnit;} public void setAgeUnit(String v){ageUnit=v;}
        public String getMobile(){return mobile;} public void setMobile(String v){mobile=v;}
        public String getEmail(){return email;} public void setEmail(String v){email=v;}
        public Integer getAge(){return age;} public void setAge(Integer v){age=v;}
    }
    public static class RefDoctorInfo {
        private Long id; private String code, name;
        public Long getId(){return id;} public void setId(Long v){id=v;}
        public String getCode(){return code;} public void setCode(String v){code=v;}
        public String getName(){return name;} public void setName(String v){name=v;}
    }
    public static class TestInfo {
        private Long id; private String code, name, type, status; private BigDecimal rate, clientRate;
        public Long getId(){return id;} public void setId(Long v){id=v;}
        public String getCode(){return code;} public void setCode(String v){code=v;}
        public String getName(){return name;} public void setName(String v){name=v;}
        public String getType(){return type;} public void setType(String v){type=v;}
        public String getStatus(){return status;} public void setStatus(String v){status=v;}
        public BigDecimal getRate(){return rate;} public void setRate(BigDecimal v){rate=v;}
        public BigDecimal getClientRate(){return clientRate;} public void setClientRate(BigDecimal v){clientRate=v;}
    }
}
