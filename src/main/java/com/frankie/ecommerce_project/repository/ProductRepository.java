package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

//    Optional<Product> findByName(String productName);

    
}
