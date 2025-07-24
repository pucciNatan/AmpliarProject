package com.example.ampliar.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "legal_guardian")
public class LegalGuardianModel extends PersonAbstract {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "legalGuardians")
    @JsonBackReference
    private List<PatientModel> patients = new ArrayList<>();

    public LegalGuardianModel(List<PatientModel> patients, String fullName, String cpf, String phoneNumber) {
        super(fullName, cpf, phoneNumber);
        setPatients(patients);
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

    public void setPatients(List<PatientModel> patients) {
        if (patients == null) {
            throw new IllegalArgumentException("A lista de pacientes não pode ser nula");
        }
        this.patients = patients;
    }
}
