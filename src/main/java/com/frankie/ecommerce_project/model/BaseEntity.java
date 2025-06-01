package com.frankie.ecommerce_project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.security.SecurityUtil;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    private String updatedBy;

    private Boolean isDeleted = false;

    @PrePersist
    private void handleCreate() {
        this.id = generateId();
        this.createdAt = Instant.now();
        if (this.createdBy == null || this.createdBy.isEmpty()) {
            this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                    ? SecurityUtil.getCurrentUserLogin().get()
                    : "system";
        }
    }

    @PreUpdate
    private void handleUpdate() {
        this.updatedAt = Instant.now();
        if (this.createdBy == null || this.createdBy.isEmpty()) {
            this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                    ? SecurityUtil.getCurrentUserLogin().get()
                    : "system";
        }
    }


    private String generateId() {
        return UUID.randomUUID().toString();
    }

}
