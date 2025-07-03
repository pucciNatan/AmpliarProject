package com.example.ampliar.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "patient")
public class PatientModel extends PersonAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_guardians",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "guardian_id")
    )
    private List<LegalGuardianModel> legalGuardians = new ArrayList<>();

    public PatientModel(LocalDate birthDate, List<LegalGuardianModel> legalGuardians,
                        String fullName, String cpf, String phoneNumber){

        super(fullName, cpf, phoneNumber);
        this.birthDate = birthDate;
        this.legalGuardians = legalGuardians;
    }
}
