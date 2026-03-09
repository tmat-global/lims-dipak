package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Test extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "type", length = 20) // Test, Package
    private String type;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "rate", precision = 10, scale = 2)
    private BigDecimal rate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sample_type", length = 100)
    private String sampleType;

    @Column(name = "turnaround_hours")
    private Integer turnaroundHours;
}
