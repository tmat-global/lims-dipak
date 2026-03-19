package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.AuthRequests;
import com.matglobal.lims.dto.response.UserResponse;
import com.matglobal.lims.entity.Role;
import com.matglobal.lims.entity.User;
import com.matglobal.lims.exception.ResourceNotFoundException;
import com.matglobal.lims.repository.RoleRepository;
import com.matglobal.lims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagementService {
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

    public UserResponse toResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId()); r.setUsername(u.getUsername());
        r.setFirstName(u.getFirstName()); r.setLastName(u.getLastName());
        r.setEmail(u.getEmail()); r.setMobile(u.getMobile()); r.setIsActive(u.getIsActive());
        r.setRoles(u.getRoles().stream().map(role -> role.getName().name().replace("ROLE_","")).collect(Collectors.toSet()));
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }
}
