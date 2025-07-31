package com.example.ampliar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record PayerUpdateDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String fullName,

        @CPF(message = "O CPF deve ser válido")
        String cpf,

        @Pattern(
                regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$",
                message = "O número de telefone deve estar no formato (11) 91234-5678")
        String phoneNumber
) {}
