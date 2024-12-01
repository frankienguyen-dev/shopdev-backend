package com.frankie.ecommerce_project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.security.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@Entity
@Builder
public class Product {
    @Id
    private String id;
    private String name;
    private Long originalPrice;
    private Long discountPrice;
    //    @Column(precision = 3, scale = 2)
//    private BigDecimal rating;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private int quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;
    private Boolean isActive = true;
    private Boolean isDeleted = false;

    @PrePersist
    private void handleCreateProduct() {
        this.id = generateId();
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }

    @PreUpdate
    private void handleUpdateProduct() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
