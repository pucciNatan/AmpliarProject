package com.example.ampliar.dto.payer;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PayerCreateDTO(
        @NotBlank(message = "O nome completo é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String fullName,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "O CPF deve ser válido")
        String cpf,

        @NotBlank(message = "O número de telefone é obrigatório")
        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "O número de telefone deve conter 10 ou 11 dígitos"
        )
        String phoneNumber
) {}
