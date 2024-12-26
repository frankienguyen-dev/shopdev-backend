package com.frankie.ecommerce_project.dto.product.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;

import java.time.Instant;

public class CreateProductResponse {

    private String id;

    private String name;

    private Long originalPrice;

    private Long discountPrice;

    private int rating;

    private String description;

    private int quantity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    private String createdBy;

    private Boolean isActive;

    private Boolean isDeleted;
}
