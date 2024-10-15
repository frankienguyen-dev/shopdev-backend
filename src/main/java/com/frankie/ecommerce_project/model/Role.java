package com.frankie.ecommerce_project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.security.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
@Builder
public class Role {
    @Id
    private String id;
    private String name;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    private List<User> user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;
    private Boolean isDeleted = false;

    @PrePersist
    private void handleCreateRole() {
        this.id = generateId();
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }

    @PreUpdate
    private void handleUpdateRole() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
