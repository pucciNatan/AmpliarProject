package com.example.ampliar.dto;

import java.util.List;

public record LegalGuardianDTO(
        Long id,
        String fullName,
        String cpf,
        String phoneNumber,
        List<Long> patientIds
) {}
