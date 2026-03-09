package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_tests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegistrationTest extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "rate", precision = 10, scale = 2)
    private BigDecimal rate;

    @Column(name = "client_rate", precision = 10, scale = 2)
    private BigDecimal clientRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    @Builder.Default
    private TestStatus status = TestStatus.PENDING;

    @Column(name = "result_value", columnDefinition = "TEXT")
    private String resultValue;

    @Column(name = "result_unit", length = 50)
    private String resultUnit;

    @Column(name = "reference_range", length = 200)
    private String referenceRange;

    @Column(name = "is_abnormal")
    private Boolean isAbnormal;

    @Column(name = "barcode", length = 50)
    private String barcode;

    @Column(name = "sample_collected_at")
    private LocalDateTime sampleCollectedAt;

    @Column(name = "sample_accepted_at")
    private LocalDateTime sampleAcceptedAt;

    @Column(name = "tested_at")
    private LocalDateTime testedAt;

    @Column(name = "authorized_at")
    private LocalDateTime authorizedAt;

    @Column(name = "authorized_by", length = 100)
    private String authorizedBy;

    public enum TestStatus { PENDING, SAMPLE_COLLECTED, SAMPLE_ACCEPTED, SAMPLE_REJECTED, TESTED, AUTHORIZED }
}
