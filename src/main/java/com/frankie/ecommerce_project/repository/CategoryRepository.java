package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE :name IS NULL OR c.name LIKE CONCAT('%', :name, '%')")
    Page<Category> searchByName(String name, Pageable pageable);
}
