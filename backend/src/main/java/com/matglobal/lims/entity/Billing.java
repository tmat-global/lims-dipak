package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billings")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "receipt_no")
    private Integer receiptNo;

    @Column(name = "bill_no")
    private Integer billNo;

    @Column(name = "bill_date")
    private LocalDateTime billDate;

    @Column(name = "amt_paid")
    private Double amtPaid;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "trans_date")
    private LocalDateTime transDate;

    @Column(name = "bill_amt")
    private Double billAmt;

    @Column(name = "dis_amt")
    private Double disAmt;

    @Column(name = "bal_amt")
    private Double balAmt;

    @Column(name = "prev_bal")
    private Double prevBal;

    @Column(name = "tax_per")
    private Double taxPer;

    @Column(name = "tax_amount")
    private Double taxAmount;

    @Column(name = "print_count")
    private Integer printCount;

    @Column(name = "is_refund")
    private Boolean isRefund;

    @Column(name = "lab_given")
    private Double labGiven;

    @Column(name = "dr_given")
    private Double drGiven;

    @Column(name = "acc_no")
    private String accNo;

    @Column(name = "chq_no")
    private String chqNo;

    @Column(name = "chq_date")
    private LocalDateTime chqDate;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_expiry_date")
    private LocalDateTime cardExpiryDate;

    @Column(name = "card_transaction_id")
    private String cardTransactionId;

    @Column(name = "online_trans_type")
    private String onlineTransType;

    @Column(name = "online_trans_id")
    private String onlineTransId;

    @Column(name = "other_charges")
    private Double otherCharges;

    @Column(name = "other_charge_remark")
    private String otherChargeRemark;

    @Column(name = "comment")
    private String comment;

    @Column(name = "bill_cancel_no")
    private Integer billCancelNo;

    @Column(name = "refund_amt")
    private Double refundAmt;

    @Column(name = "discount_perform_to")
    private Integer discountPerformTo;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "username")
    private String username;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
}
