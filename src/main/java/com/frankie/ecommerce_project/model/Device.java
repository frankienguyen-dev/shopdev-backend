package com.frankie.ecommerce_project.model;

import com.frankie.ecommerce_project.security.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "devices")
@Entity
public class Device {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String userAgent;

    private String ip;

    private Instant lastActive;

    private Instant createdAt;

    private Boolean isActive = true;


    @PrePersist
    private void handleCreate() {
        this.id = generateId();
        this.createdAt = Instant.now();
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
