package com.frankie.ecommerce_project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sku_specs")
public class SkuSpecs extends BaseEntity{
    @Column(name = "spu_specs", columnDefinition = "json")
    private String spuSpecs;
}
