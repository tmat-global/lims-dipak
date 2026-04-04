package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", unique = true, nullable = false, length = 30)
    private RoleName name;

    // ✅ FIX: Add this constructor (IMPORTANT)
    public Role(RoleName name) {
        this.name = name;
    }

    // ✅ ENUM for roles
    public enum RoleName {
        ROLE_ADMIN,
        ROLE_DOCTOR,
        ROLE_LAB_TECHNICIAN,
        ROLE_RECEPTIONIST
    }
}