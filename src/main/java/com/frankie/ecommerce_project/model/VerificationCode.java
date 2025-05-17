package com.frankie.ecommerce_project.model;

import java.time.Instant;
import java.util.UUID;

import com.frankie.ecommerce_project.utils.VerificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "verification_codes")
public class VerificationCode {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String hashedCode;

    private Instant expirationTime;

    private Integer attempts = 0;

    @Enumerated(EnumType.STRING)
    private VerificationType type;

    private Boolean isVerified = false;

    @PrePersist
    private void generateRandomId() {
        this.id = generateId();
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
