package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :roleId")
    Optional<Role> findByIdWithPermissions(String roleId);

}
