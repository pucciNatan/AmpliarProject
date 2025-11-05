package com.example.ampliar.dto.psychologist;

import java.util.List;

import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PsychologistUpdateDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String fullName,

        @CPF(message = "O CPF deve ser válido")
        String cpf,

        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "O número de telefone deve conter 10 ou 11 dígitos"
        )
        String phoneNumber,

        @Email(message = "O email deve ser válido")
        String email,

        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password,

        @Size(max = 30, message = "O CRP deve ter no máximo 30 caracteres")
        String crp,

        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
        String address,

        String bio,

        List<String> specialties,

        List<PsychologistWorkingHourDTO> workingHours
) {}
