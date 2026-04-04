package com.matglobal.lims.config;

import com.matglobal.lims.entity.*;
import com.matglobal.lims.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedDefaultAdmin();
        seedDefaultTests();
        log.info("✅ LIMS Data bootstrap complete.");
    }

    private void seedRoles() {
        for (Role.RoleName roleName : Role.RoleName.values()) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName)));
        }
    }

    private void seedDefaultAdmin() {
        if (userRepository.existsByUsername("admin"))
            return;

        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail("admin@matglobal.com");
        admin.setIsActive(true);

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);

        userRepository.save(admin);

        log.info("✅ Default Admin Created → username: admin | password: admin123");
    }

    private void seedDefaultTests() {
        if (testRepository.count() > 0)
            return;

        Object[][] tests = {
                { "CBC", "Complete Blood Count", "Test", "Haematology", 600, "Blood EDTA", 4 },
                { "WIDAL", "Widal Test", "Test", "Serology", 450, "Blood Plain", 6 },
        };

        for (Object[] t : tests) {
            Test test = new Test();
            test.setCode((String) t[0]);
            test.setName((String) t[1]);
            test.setType((String) t[2]);
            test.setDepartment((String) t[3]);
            test.setRate(BigDecimal.valueOf((int) t[4]));
            test.setSampleType((String) t[5]);
            test.setTurnaroundHours((int) t[6]);
            test.setIsActive(true);

            testRepository.save(test);
        }

        log.info("✅ Seeded default tests");
    }
}