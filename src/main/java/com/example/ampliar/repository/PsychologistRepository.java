package com.example.ampliar.repository;

import com.example.ampliar.model.PsychologistModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PsychologistRepository extends JpaRepository<PsychologistModel, Long> {
    Optional<PsychologistModel> findByEmail(String email);
}

