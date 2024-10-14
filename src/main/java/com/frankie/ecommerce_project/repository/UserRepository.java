package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User  u WHERE :email IS NULL OR u.email LIKE CONCAT('%', :email, '%')")
    Page<User> findUserByEmail(Pageable pageable, String email);
}
