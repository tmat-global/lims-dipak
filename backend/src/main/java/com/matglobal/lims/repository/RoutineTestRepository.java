package com.matglobal.lims.repository;

import com.matglobal.lims.entity.RoutineTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoutineTestRepository extends JpaRepository<RoutineTest, Long> {
    List<RoutineTest> findByIsActiveTrueOrderByTestNameAsc();
    boolean existsByTestIdAndIsActiveTrue(Long testId);
}
