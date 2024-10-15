package com.frankie.ecommerce_project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.security.SecurityUtil;
import com.frankie.ecommerce_project.security.token.JwtTokenProvider;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@Entity
public class User {
    @Id
    private String id;
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
    private List<Role> roles = new LinkedList<>();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;
    private Boolean isDeleted = false;
    private Boolean isActive = true;

    @PrePersist
    private void handleCreateUser() {
        this.id = generateId();
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }

    @PreUpdate
    private void handleUpdateUser() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }


    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
