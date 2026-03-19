package com.matglobal.lims.repository;

import com.matglobal.lims.entity.ReferringDoctor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReferringDoctorRepository extends JpaRepository<ReferringDoctor, Long> {
    Optional<ReferringDoctor> findByCode(String code);
    boolean existsByCode(String code);
    Page<ReferringDoctor> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
    List<ReferringDoctor> findByIsActiveTrueOrderByNameAsc();
}
