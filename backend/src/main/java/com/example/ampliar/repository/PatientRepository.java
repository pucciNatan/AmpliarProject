package com.example.ampliar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ampliar.model.PatientModel;
import com.example.ampliar.model.PsychologistModel;

public interface PatientRepository extends JpaRepository<PatientModel, Long> {

    List<PatientModel> findAllByPsychologistAndDeletedAtIsNull(PsychologistModel psychologist);

    Optional<PatientModel> findByIdAndPsychologistAndDeletedAtIsNull(Long id, PsychologistModel psychologist);

    List<PatientModel> findByIdInAndPsychologistAndDeletedAtIsNull(List<Long> ids, PsychologistModel psychologist);
}
