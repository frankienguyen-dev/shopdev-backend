package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, String> {
    Optional<Brand> findByName(String name);

    @Query("SELECT b FROM Brand b WHERE :name IS NULL OR b.name LIKE CONCAT('%', :name, '%')")
    Page<Brand> searchBrandByName(String name, Pageable pageable);
}
