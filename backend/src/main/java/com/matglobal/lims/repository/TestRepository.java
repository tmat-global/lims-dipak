package com.matglobal.lims.repository;

import com.matglobal.lims.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Optional<Test> findByCode(String code);
    boolean existsByCode(String code);
    List<Test> findByIsActiveTrue();
    @Query("SELECT t FROM Test t WHERE t.isActive = true AND (LOWER(t.name) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(t.code) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<Test> searchActive(@Param("q") String q);
}
