package com.example.ampliar.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "legal_guardian")
public class LegalGuardianModel extends PersonAbstract {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    @ManyToMany(mappedBy = "legalGuardians")
    @JsonBackReference
    private List<PatientModel> patients = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "psychologist_id", nullable = false)
    @JsonBackReference
    private PsychologistModel psychologist;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    public LegalGuardianModel(List<PatientModel> patients, String fullName, String cpf, String phoneNumber, PsychologistModel psychologist) {
        super(fullName, cpf, phoneNumber);
        this.patients = (patients != null) ? patients : new ArrayList<>();
        this.psychologist = psychologist;
    }

    public void setPatients(List<PatientModel> patients) {
        this.patients = (patients != null) ? patients : new ArrayList<>();
    }

    public void setPsychologist(PsychologistModel psychologist) {
        this.psychologist = psychologist;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
