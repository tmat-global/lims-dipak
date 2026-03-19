package com.matglobal.lims.repository;

import com.matglobal.lims.entity.Patient;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
    boolean existsByEmail(String email);
    @Query("SELECT p FROM Patient p WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%'))) AND (:mobile IS NULL OR p.mobile LIKE CONCAT('%',:mobile,'%'))")
    Page<Patient> searchPatients(@Param("name") String name, @Param("mobile") String mobile, Pageable pageable);
}
