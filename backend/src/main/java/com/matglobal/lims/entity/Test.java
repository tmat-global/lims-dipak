package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;
    private String type;
    private String department;

    private BigDecimal rate;

    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String sampleType;
    private Integer turnaroundHours;

    // Optional JSON fields
    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(columnDefinition = "TEXT")
    private String ranges;

    @Column(columnDefinition = "TEXT")
    private String testConfig;

    @Column(columnDefinition = "TEXT")
    private String formula;

    @Column(columnDefinition = "TEXT")
    private String reportNotes;
}