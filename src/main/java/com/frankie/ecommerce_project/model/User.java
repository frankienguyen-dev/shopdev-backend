package com.frankie.ecommerce_project.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "users")
@Entity
public class User extends BaseEntity {

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    private String phoneNumber;

    private String address;

    private String avatar;

    private LocalDate dateOfBirth;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    private Boolean isActive = true;

    private Boolean isVerified = false;

}
