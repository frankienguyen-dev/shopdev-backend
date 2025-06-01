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
@Entity
@Builder
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "device_id")
    private Device device;

    private String token;

    private Instant expiredAt;

    private Instant createdAt;

    @PrePersist
    private void handleCreate() {
        this.id = generateId();
        this.createdAt = Instant.now();
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
