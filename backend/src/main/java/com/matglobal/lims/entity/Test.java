package com.matglobal.lims.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tests")
public class Test extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "code", unique = true, nullable = false, length = 30) private String code;
    @Column(name = "name", nullable = false, length = 200) private String name;
    @Column(name = "type", length = 20) private String type;
    @Column(name = "department", length = 100) private String department;
    @Column(name = "rate", precision = 10, scale = 2) private BigDecimal rate;
    @Column(name = "is_active") private Boolean isActive = true;
    @Column(name = "description", columnDefinition = "TEXT") private String description;
    @Column(name = "sample_type", length = 100) private String sampleType;
    @Column(name = "turnaround_hours") private Integer turnaroundHours;

    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getType(){return type;} public void setType(String v){type=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
    public BigDecimal getRate(){return rate;} public void setRate(BigDecimal v){rate=v;}
    public Boolean getIsActive(){return isActive;} public void setIsActive(Boolean v){isActive=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getSampleType(){return sampleType;} public void setSampleType(String v){sampleType=v;}
    public Integer getTurnaroundHours(){return turnaroundHours;} public void setTurnaroundHours(Integer v){turnaroundHours=v;}
}
