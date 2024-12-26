package com.frankie.ecommerce_project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.security.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "categories")
@Entity
public class Category extends BaseEntity{

    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
}
