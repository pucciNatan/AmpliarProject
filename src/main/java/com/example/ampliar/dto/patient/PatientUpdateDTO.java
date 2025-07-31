package com.example.ampliar.dto.patient;

import com.example.ampliar.validation.constraints.BirthDate;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;

public record PatientUpdateDTO (
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String fullName,

        @CPF
        String cpf,

        @Pattern(
                regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$",
                message = "O número de telefone deve estar no formato (11) 91234-5678")
        String phoneNumber,

        @BirthDate
        LocalDate birthDate,

        List<Long> legalGuardianIds
){}

