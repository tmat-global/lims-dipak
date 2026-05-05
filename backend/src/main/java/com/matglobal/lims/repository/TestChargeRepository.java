package com.matglobal.lims.repository;

import com.matglobal.lims.entity.TestCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestChargeRepository extends JpaRepository<TestCharge, Long> {
    List<TestCharge> findByIsActiveTrueOrderByTestNameAsc();
    List<TestCharge> findByRateTypeNameAndIsActiveTrueOrderByTestNameAsc(String rateTypeName);
    List<TestCharge> findByRateTypeNameAndDepartmentAndIsActiveTrueOrderByTestNameAsc(String rateTypeName, String department);

    @Query("SELECT DISTINCT tc.rateTypeName FROM TestCharge tc WHERE tc.isActive = true ORDER BY tc.rateTypeName")
    List<String> findDistinctRateTypeNames();

    @Query("SELECT DISTINCT tc.department FROM TestCharge tc WHERE tc.isActive = true ORDER BY tc.department")
    List<String> findDistinctDepartments();
}
