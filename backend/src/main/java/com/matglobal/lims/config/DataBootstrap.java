package com.matglobal.lims.config;

import com.matglobal.lims.entity.*;
import com.matglobal.lims.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
        User admin = User.builder()
                .username("admin").password(passwordEncoder.encode("admin123"))
                .firstName("System").lastName("Admin")
                .email("admin@matglobal.com").isActive(true)
                .roles(Set.of(adminRole)).build();
        userRepository.save(admin);
        log.info("Default admin created: admin / admin123");
    }

    private void seedDefaultTests() {
        if (testRepository.count() > 0) return;
        Object[][] tests = {
            {"CBC",    "Complete Blood Count",                "Test",   "Haematology",  600,  "Blood EDTA",   4},
            {"WIDAL",  "Widal Test",                          "Test",   "Serology",     450,  "Blood Plain",  6},
            {"HPYLR",  "Rapid H. pylori",                     "Test",   "Serology",     500,  "Blood Serum",  2},
            {"DENGNS", "Dengue NS1 Ag",                       "Test",   "Serology",     800,  "Blood Serum",  4},
            {"HBA1C",  "HbA1c (Glycated Haemoglobin)",        "Test",   "Biochemistry", 550,  "Blood EDTA",   6},
            {"LFT",    "Liver Function Test",                  "Package","Biochemistry", 900,  "Blood Serum",  8},
            {"KFT",    "Kidney Function Test",                 "Package","Biochemistry", 850,  "Blood Serum",  8},
            {"LIPID",  "Lipid Profile",                        "Package","Biochemistry", 750,  "Blood Serum",  8},
            {"TFT",    "Thyroid Function Test",                "Package","Biochemistry", 700,  "Blood Serum",  24},
            {"FLU5P",  "Viral Respiratory Panel - 5P",         "Package","Microbiology", 1800, "Nasal Swab",   24},
            {"MALAR",  "Malaria Parasite (Smear)",             "Test",   "Haematology",  400,  "Blood EDTA",   2},
            {"URINE",  "Urine Routine & Microscopy",           "Test",   "Biochemistry", 300,  "Urine",        2},
            {"FBS",    "Fasting Blood Sugar",                  "Test",   "Biochemistry", 200,  "Blood Fluoride",2},
            {"PPBS",   "Post Prandial Blood Sugar",            "Test",   "Biochemistry", 200,  "Blood Fluoride",2},
            {"SERUM",  "Serum Electrolytes",                   "Test",   "Biochemistry", 650,  "Blood Serum",  6},
            {"ESR",    "ESR (Erythrocyte Sedimentation Rate)", "Test",   "Haematology",  250,  "Blood EDTA",   2},
            {"STOOL",  "Stool Routine & Microscopy",           "Test",   "Microbiology", 350,  "Stool",        8},
            {"URIC",   "Serum Uric Acid",                      "Test",   "Biochemistry", 250,  "Blood Serum",  4},
            {"PREG",   "Pregnancy Test (Beta HCG)",            "Test",   "Serology",     400,  "Urine/Blood",  2},
            {"HBSAG",  "HBsAg (Hepatitis B Surface Ag)",       "Test",   "Serology",     350,  "Blood Serum",  4},
        };
        for (Object[] t : tests) {
            testRepository.save(Test.builder()
                .code((String)t[0]).name((String)t[1]).type((String)t[2])
                .department((String)t[3]).rate(new BigDecimal((int)t[4]))
                .sampleType((String)t[5]).turnaroundHours((int)t[6]).isActive(true).build());
        }
        log.info("Seeded {} default tests", tests.length);
    }
}
