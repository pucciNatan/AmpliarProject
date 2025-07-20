package com.example.ampliar.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PatientDTO {
    private String fullName;
    private String cpf;
    private String phoneNumber;
    private LocalDate birthDate;
    private List<Long> legalGuardianIds;
}
