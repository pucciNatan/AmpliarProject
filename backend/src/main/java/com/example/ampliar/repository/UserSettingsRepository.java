package com.example.ampliar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ampliar.model.UserSettingsModel;

public interface UserSettingsRepository extends JpaRepository<UserSettingsModel, Long> {

    Optional<UserSettingsModel> findByPsychologistId(Long psychologistId);

}
