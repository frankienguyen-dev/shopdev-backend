package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("SELECT p FROM Product  p WHERE :name IS NULL OR p.name LIKE CONCAT('%', :name, '%')")
    Page<Product> searchProductByName(String name, Pageable pageable);
}
