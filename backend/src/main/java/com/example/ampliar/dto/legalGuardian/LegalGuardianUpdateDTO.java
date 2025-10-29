package com.example.ampliar.dto.legalGuardian;

import java.util.List;

import org.hibernate.validator.constraints.br.CPF;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LegalGuardianUpdateDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String fullName,

        @CPF(message = "O CPF deve ser válido")
        String cpf,

        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "O número de telefone deve conter 10 ou 11 dígitos"
        )
        String phoneNumber,

        List<Long> patientIds
) {}
