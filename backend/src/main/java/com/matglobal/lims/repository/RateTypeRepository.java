package com.matglobal.lims.repository;

import com.matglobal.lims.entity.RateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RateTypeRepository extends JpaRepository<RateType, Long> {
    List<RateType> findByIsActiveTrueOrderByCreatedAtDesc();
    List<RateType> findByMasterTypeAndIsActiveTrueOrderByCreatedAtDesc(String masterType);
    boolean existsByNameAndMasterTypeAndIsActiveTrue(String name, String masterType);
}
