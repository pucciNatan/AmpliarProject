package com.example.ampliar.dto.psychologist;

import java.util.List;

public record PsychologistDTO(
        Long id,
        String fullName,
        String cpf,
        String phoneNumber,
        String email,
        String crp,
        String address,
        String bio,
        List<String> specialties,
        List<PsychologistWorkingHourDTO> workingHours
) {}
