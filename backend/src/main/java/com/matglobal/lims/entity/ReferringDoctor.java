package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "referring_doctors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferringDoctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;
    private String mobile;
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;
    private String patientType;
    private String rateType;

    @Builder.Default
    private Boolean isActive = true;
}