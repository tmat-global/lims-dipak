package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_charges")
@EntityListeners(AuditingEntityListener.class)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TestCharge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rate_type_name")
    private String rateTypeName;   // e.g. MRP Rate

    @Column(name = "department")
    private String department;     // e.g. HAEMATOLOGY

    @Column(name = "test_code")
    private String testCode;       // e.g. SLION

    @Column(name = "test_name")
    private String testName;       // e.g. IONIC CALCIUM

    @Column(name = "test_id")
    private Long testId;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedDate   @Column(name = "created_at") private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
}
