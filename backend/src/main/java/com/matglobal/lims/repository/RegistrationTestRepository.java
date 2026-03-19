package com.matglobal.lims.repository;

import com.matglobal.lims.entity.RegistrationTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegistrationTestRepository extends JpaRepository<RegistrationTest, Long> {
    List<RegistrationTest> findByRegistrationId(Long registrationId);
    List<RegistrationTest> findByStatus(RegistrationTest.TestStatus status);
    @Query("SELECT rt FROM RegistrationTest rt WHERE rt.registration.id = :regId")
    List<RegistrationTest> findAllByRegistrationId(@Param("regId") Long regId);
}
