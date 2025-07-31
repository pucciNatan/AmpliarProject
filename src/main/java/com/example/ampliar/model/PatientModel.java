package com.example.ampliar.model;

import com.example.ampliar.validation.constraints.BirthDate;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "patient")
public class PatientModel extends PersonAbstract {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    @BirthDate
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
        name = "patient_guardians",
        joinColumns = @JoinColumn(name = "patient_id"),
        inverseJoinColumns = @JoinColumn(name = "guardian_id")
    )
    @JsonManagedReference
    private List<LegalGuardianModel> legalGuardians = new ArrayList<>();

    public PatientModel(LocalDate birthDate, List<LegalGuardianModel> legalGuardians,
                        String fullName, String cpf, String phoneNumber) {
        super(fullName, cpf, phoneNumber);
        this.birthDate = birthDate;
        this.legalGuardians = legalGuardians != null ? legalGuardians : new ArrayList<>();
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

    public void setBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode ser no futuro");
        }
        this.birthDate = birthDate;
    }

    public void setLegalGuardians(List<LegalGuardianModel> legalGuardians) {
        if (legalGuardians == null || legalGuardians.isEmpty()) {
            throw new IllegalArgumentException("O paciente deve ter pelo menos um responsável");
        }
        this.legalGuardians = legalGuardians;
    }
}
