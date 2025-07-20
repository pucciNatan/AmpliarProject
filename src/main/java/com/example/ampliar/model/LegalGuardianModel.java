package com.example.ampliar.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "legal_guardian")
public class LegalGuardianModel extends PersonAbstract {
    @ManyToMany(mappedBy = "legalGuardians")
    @JsonBackReference
    private List<PatientModel> patients = new ArrayList<>();

    public LegalGuardianModel(List<PatientModel> patients,
                        String fullName, String cpf, String phoneNumber){

        super(fullName, cpf, phoneNumber);
        this.patients = patients;
    }
}
