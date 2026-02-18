package com.example.ampliar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ampliar.model.PsychologistModel;

public interface PsychologistRepository extends JpaRepository<PsychologistModel, Long> {
    Optional<PsychologistModel> findByEmail(String email);
    Optional<PsychologistModel> findByCpf(String cpf);

    Optional<PsychologistModel> findByEmailAndDeletedAtIsNull(String email);
    Optional<PsychologistModel> findByCpfAndDeletedAtIsNull(String cpf);
    List<PsychologistModel> findAllByDeletedAtIsNull();
    Optional<PsychologistModel> findByIdAndDeletedAtIsNull(Long id);
}
