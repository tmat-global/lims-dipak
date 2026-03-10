package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.AuthRequests;
import com.matglobal.lims.dto.response.AuthResponse;
import com.matglobal.lims.dto.response.UserResponse;
import com.matglobal.lims.entity.Role;
import com.matglobal.lims.entity.User;
import com.matglobal.lims.exception.BusinessException;
import com.matglobal.lims.exception.DuplicateResourceException;
import com.matglobal.lims.repository.RoleRepository;
import com.matglobal.lims.repository.UserRepository;
import com.matglobal.lims.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setType("Bearer");
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFirstName() + " " + user.getLastName());
        response.setEmail(user.getEmail());
        response.setRoles(roles);
        return response;
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

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setMobile(req.getMobile());
        user.setIsActive(true);
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return toUserResponse(user);
    }

    public UserResponse toUserResponse(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setFirstName(u.getFirstName());
        r.setLastName(u.getLastName());
        r.setEmail(u.getEmail());
        r.setMobile(u.getMobile());
        r.setIsActive(u.getIsActive());
        r.setRoles(u.getRoles().stream()
                .map(role -> role.getName().name().replace("ROLE_", ""))
                .collect(Collectors.toSet()));
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }
}
