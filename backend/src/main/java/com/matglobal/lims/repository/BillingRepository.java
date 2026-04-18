package com.matglobal.lims.repository;

import com.matglobal.lims.entity.Billing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    @Query("""
        SELECT b FROM Billing b 
        WHERE (:from IS NULL OR b.billDate >= :from)
        AND (:to IS NULL OR b.billDate <= :to)
        AND (:name IS NULL OR LOWER(b.patient.name) LIKE LOWER(CONCAT('%',:name,'%')))
        AND (:mobile IS NULL OR b.patient.mobile LIKE CONCAT('%',:mobile,'%'))
        AND (:regNo IS NULL OR b.registration.regNo LIKE CONCAT('%',:regNo,'%'))
        AND (:paymentType IS NULL OR b.paymentType = :paymentType)
    """)
    Page<Billing> searchBillings(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        @Param("name") String name,
        @Param("mobile") String mobile,
        @Param("regNo") String regNo,
        @Param("paymentType") String paymentType,
        Pageable pageable
    );

    @Query("""
        SELECT b FROM Billing b 
        WHERE b.billDate >= :from AND b.billDate <= :to
    """)
    List<Billing> findByDateRange(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    List<Billing> findByRegistrationId(Long registrationId);
}
