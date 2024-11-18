package com.frankie.ecommerce_project.model;

import jakarta.persistence.Column;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private String id;
    private String name;
    private Long originalPrice;
    private Long discountPrice;
    private int rating;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private int quantity;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private Boolean isActive = true;
    private Boolean isDeleted = false;
}
