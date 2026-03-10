package com.matglobal.lims.config;

import com.matglobal.lims.entity.*;
import com.matglobal.lims.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TestRepository testRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedDefaultAdmin();
        seedDefaultTests();
        log.info("MAT Global LIMS — Data bootstrap complete.");
    }

    private void seedRoles() {
        for (Role.RoleName rn : Role.RoleName.values()) {
            roleRepository.findByName(rn).orElseGet(() -> {
                Role r = new Role(); r.setName(rn); return roleRepository.save(r);
            });
        }
    }

    private void seedDefaultAdmin() {
        if (userRepository.existsByUsername("admin")) return;
        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElseThrow();
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail("admin@matglobal.com");
        admin.setIsActive(true);
        admin.setRoles(roles);
        userRepository.save(admin);
        log.info("Default admin created: admin / admin123");
    }

    private void seedDefaultTests() {
        if (testRepository.count() > 0) return;
        Object[][] tests = {
            {"CBC",    "Complete Blood Count",                "Test",    "Haematology",   600,  "Blood EDTA",    4},
            {"WIDAL",  "Widal Test",                          "Test",    "Serology",      450,  "Blood Plain",   6},
            {"HPYLR",  "Rapid H. pylori",                     "Test",    "Serology",      500,  "Blood Serum",   2},
            {"DENGNS", "Dengue NS1 Ag",                       "Test",    "Serology",      800,  "Blood Serum",   4},
            {"HBA1C",  "HbA1c (Glycated Haemoglobin)",        "Test",    "Biochemistry",  550,  "Blood EDTA",    6},
            {"LFT",    "Liver Function Test",                  "Package", "Biochemistry",  900,  "Blood Serum",   8},
            {"KFT",    "Kidney Function Test",                 "Package", "Biochemistry",  850,  "Blood Serum",   8},
            {"LIPID",  "Lipid Profile",                        "Package", "Biochemistry",  750,  "Blood Serum",   8},
            {"TFT",    "Thyroid Function Test",                "Package", "Biochemistry",  700,  "Blood Serum",  24},
            {"FLU5P",  "Viral Respiratory Panel",              "Package", "Microbiology", 1800,  "Nasal Swab",   24},
            {"MALAR",  "Malaria Parasite (Smear)",             "Test",    "Haematology",   400,  "Blood EDTA",    2},
            {"URINE",  "Urine Routine & Microscopy",           "Test",    "Biochemistry",  300,  "Urine",         2},
            {"FBS",    "Fasting Blood Sugar",                  "Test",    "Biochemistry",  200,  "Blood Fluoride",2},
            {"PPBS",   "Post Prandial Blood Sugar",            "Test",    "Biochemistry",  200,  "Blood Fluoride",2},
            {"SERUM",  "Serum Electrolytes",                   "Test",    "Biochemistry",  650,  "Blood Serum",   6},
            {"ESR",    "ESR",                                  "Test",    "Haematology",   250,  "Blood EDTA",    2},
            {"HBSAG",  "HBsAg (Hepatitis B Surface Ag)",       "Test",    "Serology",      350,  "Blood Serum",   4},
        };
        for (Object[] t : tests) {
            Test test = new Test();
            test.setCode((String) t[0]);
            test.setName((String) t[1]);
            test.setType((String) t[2]);
            test.setDepartment((String) t[3]);
            test.setRate(new BigDecimal((int) t[4]));
            test.setSampleType((String) t[5]);
            test.setTurnaroundHours((int) t[6]);
            test.setIsActive(true);
            testRepository.save(test);
        }
        log.info("Seeded {} default tests", tests.length);
    }
}
