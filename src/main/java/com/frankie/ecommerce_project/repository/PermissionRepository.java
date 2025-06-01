package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    Optional<Permission> findByName(String perrmissionName);

}
