package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    Optional<Permission> findByName(String perrmissioNName);

}
