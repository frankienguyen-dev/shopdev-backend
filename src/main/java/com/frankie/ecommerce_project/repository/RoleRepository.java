package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role  r WHERE :name IS NULL OR r.name LIKE CONCAT('%', :name, '%')")
    Page<Role> searchRoleByName(String name, Pageable pageable);
}
