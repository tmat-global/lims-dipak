package com.matglobal.lims.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "salutation", length = 10) private String salutation;
    @Column(name = "name", nullable = false, length = 150) private String name;
    @Column(name = "gender", length = 10) private String gender;
    @Column(name = "age") private Integer age;
    @Column(name = "age_unit", length = 10) private String ageUnit;
    @Column(name = "date_of_birth") private LocalDate dateOfBirth;
    @Column(name = "mobile", length = 20) private String mobile;
    @Column(name = "alternate_mobile", length = 20) private String alternateMobile;
    @Column(name = "email", length = 150) private String email;
    @Column(name = "address", columnDefinition = "TEXT") private String address;
    @Column(name = "remarks", columnDefinition = "TEXT") private String remarks;
    @Column(name = "passport_no", length = 50) private String passportNo;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations = new ArrayList<>();

    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getSalutation(){return salutation;} public void setSalutation(String v){salutation=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public Integer getAge(){return age;} public void setAge(Integer v){age=v;}
    public String getAgeUnit(){return ageUnit;} public void setAgeUnit(String v){ageUnit=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
    public String getMobile(){return mobile;} public void setMobile(String v){mobile=v;}
    public String getAlternateMobile(){return alternateMobile;} public void setAlternateMobile(String v){alternateMobile=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getRemarks(){return remarks;} public void setRemarks(String v){remarks=v;}
    public String getPassportNo(){return passportNo;} public void setPassportNo(String v){passportNo=v;}
    public List<Registration> getRegistrations(){return registrations;} public void setRegistrations(List<Registration> v){registrations=v;}
}
