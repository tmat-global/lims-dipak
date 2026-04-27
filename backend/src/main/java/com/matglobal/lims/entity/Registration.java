package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "registrations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "patient", "registrationTests" })
public class Registration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String regNo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReferringDoctor refDoctor;

    private String patientType;
    private String center;
    private String paymentType;

    private BigDecimal totalAmount;
    private BigDecimal otherCharges;
    private BigDecimal discountAmount;
    private String discountType;

    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Builder.Default
    private Boolean notifyOnLab = true;
    @Builder.Default
    private Boolean notifyEmail = false;
    @Builder.Default
    private Boolean notifyWhatsapp = false;
    @Builder.Default
    private Boolean isEmergency = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<RegistrationTest> registrationTests = new ArrayList<>();

    public enum RegistrationStatus {
        REGISTERED, SAMPLE_COLLECTED, TESTED, COMPLETED
    }
}