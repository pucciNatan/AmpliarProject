package com.example.ampliar.dto;

import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LegalGuardianCreateDTO(
        @NotBlank(message = "O nome completo é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String fullName,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "O CPF deve ser válido")
        String cpf,

        @NotBlank(message = "O número de telefone é obrigatório")
        @Pattern(
                regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$",
                message = "O número de telefone deve estar no formato (11) 91234-5678")
        String phoneNumber,

        @NotEmpty(message = "É necessário associar pelo menos um paciente")
        List<Long> patientIds
) {}
