package com.matglobal.lims.repository;

import com.matglobal.lims.entity.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {
    List<Center> findByIsActiveTrue();
    Optional<Center> findByCode(String code);
    boolean existsByCode(String code);
}
