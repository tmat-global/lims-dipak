package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationTest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Registration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    private Test test;

    private BigDecimal rate;
    private BigDecimal clientRate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TestStatus status = TestStatus.PENDING;

    private String resultValue;
    private String resultUnit;
    private String referenceRange;

    private Boolean isAbnormal;

    private LocalDateTime testedAt;

    public enum TestStatus {
        PENDING, TESTED, AUTHORIZED, COMPLETED, SAMPLE_COLLECTED, SAMPLE_ACCEPTED, SAMPLE_REJECTED, DISPATCHED
    }
}