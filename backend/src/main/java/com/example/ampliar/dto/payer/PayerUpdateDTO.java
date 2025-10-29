package com.example.ampliar.dto.payer;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record PayerUpdateDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String fullName,

        @CPF(message = "O CPF deve ser válido")
        String cpf,

        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "O número de telefone deve conter 10 ou 11 dígitos"
        )
        String phoneNumber
) {}
