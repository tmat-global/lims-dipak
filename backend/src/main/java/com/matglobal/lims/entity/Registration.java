package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registrations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Registration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reg_no", unique = true, nullable = false, length = 20)
    private String regNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_doctor_id")
    private ReferringDoctor refDoctor;

    @Column(name = "patient_type", length = 20)
    private String patientType; // OPD, IPD, Corporate

    @Column(name = "center", length = 100)
    private String center;

    @Column(name = "payment_type", length = 20)
    private String paymentType; // Cash, Card, Cheque, Online

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "other_charges", precision = 10, scale = 2)
    private BigDecimal otherCharges;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "discount_type", length = 10) // Amt, Per%
    private String discountType;

    @Column(name = "net_amount", precision = 12, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "balance_amount", precision = 12, scale = 2)
    private BigDecimal balanceAmount;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "notify_on_lab")
    @Builder.Default
    private Boolean notifyOnLab = true;

    @Column(name = "notify_email")
    @Builder.Default
    private Boolean notifyEmail = false;

    @Column(name = "notify_whatsapp")
    @Builder.Default
    private Boolean notifyWhatsapp = false;

    @Column(name = "is_emergency")
    @Builder.Default
    private Boolean isEmergency = false;

    @Column(name = "prescription_path", length = 500)
    private String prescriptionPath;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RegistrationTest> registrationTests = new ArrayList<>();

    public enum RegistrationStatus {
        REGISTERED, SAMPLE_COLLECTED, SAMPLE_ACCEPTED, SAMPLE_REJECTED,
        TESTED, AUTHORIZED, COMPLETED, DISPATCHED
    }
}
