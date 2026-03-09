package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "salutation", length = 10)
    private String salutation;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "age_unit", length = 10)
    private String ageUnit;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "alternate_mobile", length = 20)
    private String alternateMobile;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "passport_no", length = 50)
    private String passportNo;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Registration> registrations = new ArrayList<>();
}
