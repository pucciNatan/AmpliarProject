package com.example.ampliar.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.ampliar.validation.constraints.BirthDate;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

    @ManyToMany(mappedBy = "patients", fetch = FetchType.LAZY)
    private List<AppointmentModel> appointments = new ArrayList<>();

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
