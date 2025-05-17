package com.frankie.ecommerce_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product extends BaseEntity{

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_desc")
    private String productDesc;

    @Column(name = "product_status")
    private Integer productStatus;

    @Column(name = "product_attrs", columnDefinition = "json")
    private String productAttrs;


    // Mối quan hệ nhiều-nhiều với SKU thông qua bảng trung gian `spu_to_sku`
    @ManyToMany
    @JoinTable(
            name = "spu_to_sku",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "sku_id", referencedColumnName = "id")
    )
    private List<SkuProduct> skus = new ArrayList<>();

}
