package com.example.ampliar.dto.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PatientDTO(
        Long id,
        String fullName,
        String cpf,
        String phoneNumber,
        LocalDate birthDate,
        List<Long> legalGuardianIds,
        String status,
        Integer totalAppointments,
        LocalDateTime lastAppointmentDate
){}
