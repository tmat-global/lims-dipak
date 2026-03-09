package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.*;
import com.matglobal.lims.dto.response.*;
import com.matglobal.lims.entity.*;
import com.matglobal.lims.exception.*;
import com.matglobal.lims.repository.*;
import com.matglobal.lims.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// ════════════════════════════════════════════════════════
//  AUTH SERVICE
// ════════════════════════════════════════════════════════
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequests.LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        String token = jwtProvider.generateToken(auth);
        User user = userRepository.findByUsername(req.getUsername()).orElseThrow();
        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name().replace("ROLE_", ""))
                .collect(Collectors.toSet());
        return AuthResponse.builder()
                .token(token).type("Bearer").id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail()).roles(roles).build();
    }

    @Transactional
    public UserResponse registerUser(AuthRequests.RegisterUserRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new DuplicateResourceException("Username already taken: " + req.getUsername());
        if (userRepository.existsByEmail(req.getEmail()))
            throw new DuplicateResourceException("Email already registered");

        Role.RoleName roleName = Role.RoleName.valueOf("ROLE_" + req.getRole().toUpperCase());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BusinessException("Invalid role: " + req.getRole()));

        User user = User.builder()
                .username(req.getUsername()).password(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName()).lastName(req.getLastName())
                .email(req.getEmail()).mobile(req.getMobile())
                .isActive(true).roles(Set.of(role)).build();
        userRepository.save(user);
        return mapUserResponse(user);
    }

    public UserResponse mapUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId()).username(u.getUsername())
                .firstName(u.getFirstName()).lastName(u.getLastName())
                .email(u.getEmail()).mobile(u.getMobile()).isActive(u.getIsActive())
                .roles(u.getRoles().stream().map(r -> r.getName().name().replace("ROLE_","")).collect(Collectors.toSet()))
                .createdAt(u.getCreatedAt()).build();
    }

    private UserRepository getUserRepository() { return userRepository; }
    private RoleRepository getRoleRepository() { return roleRepository; }
    private PasswordEncoder getPE() { return passwordEncoder; }
}
