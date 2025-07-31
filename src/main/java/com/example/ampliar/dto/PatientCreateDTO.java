package com.example.ampliar.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.ampliar.validation.constraints.BirthDate;

public record PatientCreateDTO(
        String fullName,
        String cpf,
        String phoneNumber,
        @BirthDate
        LocalDate birthDate,
        List<Long> legalGuardianIds
){}
