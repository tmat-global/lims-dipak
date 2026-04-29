package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "routine_tests")
@EntityListeners(AuditingEntityListener.class)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoutineTest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_code") private String testCode;
    @Column(name = "test_name") private String testName;
    @Column(name = "test_id")   private Long testId;

    @Builder.Default
    @Column(name = "is_active") private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at") private LocalDateTime createdAt;
}
