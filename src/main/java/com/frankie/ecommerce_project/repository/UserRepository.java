package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE :email IS NULL OR u.email LIKE CONCAT('%', :email, '%')")
    Page<User> findUserByEmailWithRoles(String email, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    Page<User> findAllWithRoles(Pageable pageable);

    @Query("SELECT DISTINCT u FROM User  u LEFT JOIN FETCH u.roles WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@NonNull String userId);

}
