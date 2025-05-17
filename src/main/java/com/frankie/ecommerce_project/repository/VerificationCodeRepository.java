package com.frankie.ecommerce_project.repository;

import com.frankie.ecommerce_project.model.User;
import com.frankie.ecommerce_project.model.VerificationCode;
import com.frankie.ecommerce_project.utils.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

    Optional<VerificationCode> findByUserAndType(User user, VerificationType type);

}
