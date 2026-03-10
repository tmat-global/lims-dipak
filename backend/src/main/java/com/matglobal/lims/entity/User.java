package com.matglobal.lims.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "username", unique = true, nullable = false, length = 80) private String username;
    @Column(name = "password", nullable = false) private String password;
    @Column(name = "first_name", length = 80) private String firstName;
    @Column(name = "last_name", length = 80) private String lastName;
    @Column(name = "email", unique = true, length = 150) private String email;
    @Column(name = "mobile", length = 20) private String mobile;
    @Column(name = "is_active") private Boolean isActive = true;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;}
    public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getMobile(){return mobile;} public void setMobile(String v){mobile=v;}
    public Boolean getIsActive(){return isActive;} public void setIsActive(Boolean v){isActive=v;}
    public Set<Role> getRoles(){return roles;} public void setRoles(Set<Role> v){roles=v;}
}
