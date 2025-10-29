package com.example.ampliar.dto.patient;

import java.time.LocalDate;
import java.util.List;

public record PatientDTO(
        Long id,
        String fullName,
        String cpf,
        String phoneNumber,
        LocalDate birthDate,
        List<Long> legalGuardianIds
){}
