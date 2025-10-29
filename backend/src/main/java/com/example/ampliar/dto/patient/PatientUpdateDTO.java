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
                regexp = "^\\d{10,11}$",
                message = "O número de telefone deve conter 10 ou 11 dígitos"
        )
        String phoneNumber,

        @BirthDate
        LocalDate birthDate,

        List<Long> legalGuardianIds
){}

