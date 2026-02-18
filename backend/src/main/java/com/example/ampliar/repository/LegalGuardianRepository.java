package com.example.ampliar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PsychologistModel;

public interface LegalGuardianRepository extends JpaRepository<LegalGuardianModel, Long>{

    List<LegalGuardianModel> findAllByPsychologistAndDeletedAtIsNull(PsychologistModel psychologist);
    Optional<LegalGuardianModel> findByIdAndPsychologistAndDeletedAtIsNull(Long id, PsychologistModel psychologist);
    List<LegalGuardianModel> findByIdInAndPsychologistAndDeletedAtIsNull(List<Long> ids, PsychologistModel psychologist);
}
