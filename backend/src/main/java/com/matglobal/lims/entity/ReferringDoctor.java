package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "referring_doctors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReferringDoctor extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "patient_type", length = 20)
    private String patientType;

    @Column(name = "rate_type", length = 50)
    private String rateType;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
