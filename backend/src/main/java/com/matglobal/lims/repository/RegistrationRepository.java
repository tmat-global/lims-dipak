package com.matglobal.lims.repository;

import com.matglobal.lims.entity.Registration;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByRegNo(String regNo);
    boolean existsByRegNo(String regNo);
    @Query("SELECT r FROM Registration r WHERE r.createdAt BETWEEN :from AND :to AND (:patientName IS NULL OR LOWER(r.patient.name) LIKE LOWER(CONCAT('%',:patientName,'%'))) AND (:mobile IS NULL OR r.patient.mobile LIKE CONCAT('%',:mobile,'%')) AND (:regNo IS NULL OR r.regNo LIKE CONCAT('%',:regNo,'%')) AND (:status IS NULL OR r.status = :status)")
    Page<Registration> searchRegistrations(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("patientName") String patientName, @Param("mobile") String mobile, @Param("regNo") String regNo, @Param("status") Registration.RegistrationStatus status, Pageable pageable);
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.createdAt >= :startOfDay")
    long countTodayRegistrations(@Param("startOfDay") LocalDateTime startOfDay);
    @Query("SELECT COALESCE(SUM(r.paidAmount), 0) FROM Registration r WHERE r.createdAt >= :startOfDay")
    BigDecimal sumTodayCollection(@Param("startOfDay") LocalDateTime startOfDay);
}
