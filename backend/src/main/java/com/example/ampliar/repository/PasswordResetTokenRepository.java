package com.example.ampliar.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ampliar.model.PasswordResetTokenModel;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenModel, Long> {

    Optional<PasswordResetTokenModel> findByTokenAndUsedFalse(String token);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
