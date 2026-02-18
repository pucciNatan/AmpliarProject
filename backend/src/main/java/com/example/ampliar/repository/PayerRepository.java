package com.example.ampliar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ampliar.model.PayerModel;
import com.example.ampliar.model.PsychologistModel;

public interface PayerRepository  extends JpaRepository<PayerModel, Long> {

    List<PayerModel> findAllByPsychologistAndDeletedAtIsNull(PsychologistModel psychologist);
    Optional<PayerModel> findByIdAndPsychologistAndDeletedAtIsNull(Long id, PsychologistModel psychologist);
    Optional<PayerModel> findByIdAndDeletedAtIsNull(Long id);
}
