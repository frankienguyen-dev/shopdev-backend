package com.frankie.ecommerce_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sku_products")
public class SkuProduct extends BaseEntity{
    @Column(name = "sku_no", unique = true)
    private String skuNo;

    @Column(name = "sku_name")
    private String skuName;

    @Column(name = "sku_description")
    private String skuDescription;

    @Column(name = "sku_stock")
    private Integer skuStock;

    @Column(name = "sku_price")
    private Double skuPrice;

    // Quan hệ nhiều-nhiều với Product qua bảng `spu_to_sku`
    @ManyToMany(mappedBy = "skus")
    private List<Product> products;

    // Quan hệ một-một với SkuAttr
    @OneToOne(mappedBy = "sku", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SkuAttribute skuAttr;
}
