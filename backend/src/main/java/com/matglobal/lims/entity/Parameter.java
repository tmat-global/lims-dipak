package com.matglobal.lims.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parameters")
@Getter
@Setter
public class Parameter extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 200)
    private String name;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "default_result", length = 100)
    private String defaultResult;

    @Column(name = "order_num")
    private Integer orderNum = 0;

    @Column(name = "active")
    private Boolean active = true;
}
