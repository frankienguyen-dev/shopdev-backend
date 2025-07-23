package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findByName(String permissionName);

    @Query("SELECT p FROM Permission p WHERE p.name IN :names")
    List<Permission> findByNameIn(List<String> names);

}
