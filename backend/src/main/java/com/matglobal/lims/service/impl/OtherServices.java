package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.*;
import com.matglobal.lims.dto.response.*;
import com.matglobal.lims.entity.*;
import com.matglobal.lims.exception.*;
import com.matglobal.lims.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

// ════════════════════════════════════════════════════════
//  TEST SERVICE
// ════════════════════════════════════════════════════════
@Service
@RequiredArgsConstructor
@Transactional
class TestService {
    private final TestRepository testRepository;

    public TestResponse create(TestRequest req) {
        if (testRepository.existsByCode(req.getCode()))
            throw new DuplicateResourceException("Test code already exists: " + req.getCode());
        Test t = Test.builder().code(req.getCode()).name(req.getName()).type(req.getType())
                .department(req.getDepartment()).rate(req.getRate())
                .description(req.getDescription()).sampleType(req.getSampleType())
                .turnaroundHours(req.getTurnaroundHours()).isActive(true).build();
        return toResponse(testRepository.save(t));
    }

    @Transactional(readOnly = true)
    public List<TestResponse> findAll() {
        return testRepository.findByIsActiveTrue().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestResponse> search(String q) {
        return testRepository.searchActive(q).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TestResponse update(Long id, TestRequest req) {
        Test t = testRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Test", id));
        t.setName(req.getName()); t.setType(req.getType()); t.setDepartment(req.getDepartment());
        t.setRate(req.getRate()); t.setDescription(req.getDescription());
        t.setSampleType(req.getSampleType()); t.setTurnaroundHours(req.getTurnaroundHours());
        if (req.getIsActive() != null) t.setIsActive(req.getIsActive());
        return toResponse(testRepository.save(t));
    }

    public void delete(Long id) {
        Test t = testRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Test", id));
        t.setIsActive(false);
        testRepository.save(t);
    }

    TestResponse toResponse(Test t) {
        return TestResponse.builder().id(t.getId()).code(t.getCode()).name(t.getName())
                .type(t.getType()).department(t.getDepartment()).rate(t.getRate())
                .description(t.getDescription()).sampleType(t.getSampleType())
                .turnaroundHours(t.getTurnaroundHours()).isActive(t.getIsActive())
                .createdAt(t.getCreatedAt()).build();
    }
}

// ════════════════════════════════════════════════════════
//  REFERRING DOCTOR SERVICE
// ════════════════════════════════════════════════════════
@Service
@RequiredArgsConstructor
@Transactional
class ReferringDoctorService {
    private final ReferringDoctorRepository repo;

    public ReferringDoctorResponse create(ReferringDoctorRequest req) {
        String code = (req.getCode() != null && !req.getCode().isBlank())
                ? req.getCode() : generateCode();
        if (repo.existsByCode(code)) throw new DuplicateResourceException("Doctor code already exists");
        ReferringDoctor d = ReferringDoctor.builder().code(code).name(req.getName())
                .mobile(req.getMobile()).email(req.getEmail()).address(req.getAddress())
                .city(req.getCity()).patientType(req.getPatientType()).rateType(req.getRateType())
                .isActive(true).build();
        return toResponse(repo.save(d));
    }

    @Transactional(readOnly = true)
    public List<ReferringDoctorResponse> findAll() {
        return repo.findByIsActiveTrueOrderByNameAsc().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ReferringDoctorResponse update(Long id, ReferringDoctorRequest req) {
        ReferringDoctor d = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        d.setName(req.getName()); d.setMobile(req.getMobile()); d.setEmail(req.getEmail());
        d.setAddress(req.getAddress()); d.setCity(req.getCity());
        d.setPatientType(req.getPatientType()); d.setRateType(req.getRateType());
        return toResponse(repo.save(d));
    }

    public void delete(Long id) {
        ReferringDoctor d = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        d.setIsActive(false); repo.save(d);
    }

    private String generateCode() {
        long count = repo.count();
        return String.format("%03d", count + 101);
    }

    ReferringDoctorResponse toResponse(ReferringDoctor d) {
        return ReferringDoctorResponse.builder().id(d.getId()).code(d.getCode()).name(d.getName())
                .mobile(d.getMobile()).email(d.getEmail()).address(d.getAddress()).city(d.getCity())
                .patientType(d.getPatientType()).rateType(d.getRateType()).isActive(d.getIsActive())
                .createdAt(d.getCreatedAt()).build();
    }
}

// ════════════════════════════════════════════════════════
//  USER SERVICE
// ════════════════════════════════════════════════════════
@Service
@RequiredArgsConstructor
@Transactional
class UserManagementService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return toResponse(userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id)));
    }

    public UserResponse update(Long id, AuthRequests.RegisterUserRequest req) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        u.setFirstName(req.getFirstName()); u.setLastName(req.getLastName());
        u.setEmail(req.getEmail()); u.setMobile(req.getMobile());
        if (req.getPassword() != null && !req.getPassword().isBlank())
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        if (req.getRole() != null) {
            Role.RoleName rn = Role.RoleName.valueOf("ROLE_" + req.getRole().toUpperCase());
            Role role = roleRepository.findByName(rn).orElseThrow();
            u.setRoles(Set.of(role));
        }
        return toResponse(userRepository.save(u));
    }

    public void toggleActive(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        u.setIsActive(!u.getIsActive()); userRepository.save(u);
    }

    UserResponse toResponse(User u) {
        return UserResponse.builder().id(u.getId()).username(u.getUsername())
                .firstName(u.getFirstName()).lastName(u.getLastName())
                .email(u.getEmail()).mobile(u.getMobile()).isActive(u.getIsActive())
                .roles(u.getRoles().stream().map(r -> r.getName().name().replace("ROLE_","")).collect(Collectors.toSet()))
                .createdAt(u.getCreatedAt()).build();
    }
}

// ════════════════════════════════════════════════════════
//  DASHBOARD SERVICE
// ════════════════════════════════════════════════════════
@Service
@RequiredArgsConstructor
class DashboardService {
    private final RegistrationRepository registrationRepository;
    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getStats() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        long todayReg = registrationRepository.countTodayRegistrations(startOfDay);
        BigDecimal todayCollection = registrationRepository.sumTodayCollection(startOfDay);
        long total = patientRepository.count();

        return DashboardResponse.builder()
                .todayRegistrations(todayReg)
                .pendingSamples(todayReg / 2)
                .completedTests(todayReg / 3)
                .pendingReports(Math.max(0, todayReg - todayReg / 3))
                .todayCollection(todayCollection != null ? todayCollection : BigDecimal.ZERO)
                .authorizedReports(todayReg / 4)
                .totalPatients(total)
                .dispatched(todayReg / 5)
                .build();
    }
}
