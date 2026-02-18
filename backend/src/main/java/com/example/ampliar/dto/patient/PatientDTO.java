package com.example.ampliar.dto.patient;

import java.time.LocalDate;
import java.util.List;

public record PatientDTO(
        Long id,
        String fullName,
        String phone,
        String email,
        String cpf,
        LocalDate birthDate,
        String address,
        String notes,
        LocalDate firstConsultationDate,
        List<Long> legalGuardianIds,
        Integer totalAppointments
) {
}
