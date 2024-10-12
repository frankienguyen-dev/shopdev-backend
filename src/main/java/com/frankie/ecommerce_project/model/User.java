package com.frankie.ecommerce_project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private Instant deletedAt;
    private String deletedBy;
    private Boolean isActive;

    @PrePersist
    private void setRandomId() {
        this.id = generateId();
        this.createdAt = Instant.now();
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
