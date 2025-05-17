package com.frankie.ecommerce_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sku_attributes")
public class SkuAttribute extends BaseEntity{

    @Column(name = "sku_no", unique = true)
    private String skuNo;

    @Column(name = "sku_stock")
    private Integer skuStock;

    @Column(name = "sku_price")
    private Long skuPrice;

    @Column(name = "sku_attrs", columnDefinition = "json")
    private String skuAttrs;

    // Quan hệ một-một với SKU
    @OneToOne
    @JoinColumn(name = "sku_id", referencedColumnName = "id")
    private SkuProduct sku;

}