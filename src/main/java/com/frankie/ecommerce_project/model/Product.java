package com.frankie.ecommerce_project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "products")
public class Product extends BaseEntity{

    private String name;

    private Long originalPrice;

    private Long discountPrice;

    private int rating;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private int quantity;

    private Boolean isActive = true;
}
