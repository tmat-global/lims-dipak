package com.matglobal.lims.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "referring_doctors")
public class ReferringDoctor extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "code", unique = true, nullable = false, length = 20) private String code;
    @Column(name = "name", nullable = false, length = 150) private String name;
    @Column(name = "mobile", length = 20) private String mobile;
    @Column(name = "email", length = 150) private String email;
    @Column(name = "address", columnDefinition = "TEXT") private String address;
    @Column(name = "city", length = 100) private String city;
    @Column(name = "patient_type", length = 20) private String patientType;
    @Column(name = "rate_type", length = 50) private String rateType;
    @Column(name = "is_active") private Boolean isActive = true;

    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getMobile(){return mobile;} public void setMobile(String v){mobile=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getCity(){return city;} public void setCity(String v){city=v;}
    public String getPatientType(){return patientType;} public void setPatientType(String v){patientType=v;}
    public String getRateType(){return rateType;} public void setRateType(String v){rateType=v;}
    public Boolean getIsActive(){return isActive;} public void setIsActive(Boolean v){isActive=v;}
}
