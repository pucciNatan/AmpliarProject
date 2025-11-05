package com.example.ampliar.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "psychologist")
public class PsychologistModel extends PersonAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 30)
    private String crp;

    @Column(length = 255)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "psychologist_specialties", joinColumns = @JoinColumn(name = "psychologist_id"))
    @Column(name = "specialty", length = 120)
    private List<String> specialties = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "psychologist_working_hours", joinColumns = @JoinColumn(name = "psychologist_id"))
    private List<PsychologistWorkingHour> workingHours = new ArrayList<>();

    public PsychologistModel(String fullName, String cpf, String phoneNumber, String email, String password) {
        super(fullName, cpf, phoneNumber);
        setEmail(email);
        setPassword(password);
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail é obrigatório");
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("O e-mail deve ser válido");
        }
        this.email = email.trim().toLowerCase();
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("A senha deve conter no mínimo 6 caracteres");
        }
        this.password = password;
    }

    public void setCrp(String crp) {
        if (crp == null || crp.trim().isEmpty()) {
            this.crp = null;
            return;
        }
        this.crp = crp.trim();
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            this.address = null;
            return;
        }
        this.address = address.trim();
    }

    public void setBio(String bio) {
        if (bio == null || bio.trim().isEmpty()) {
            this.bio = null;
            return;
        }
        this.bio = bio.trim();
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties.clear();
        if (specialties != null) {
            this.specialties.addAll(specialties);
        }
    }

    public void setWorkingHours(List<PsychologistWorkingHour> workingHours) {
        this.workingHours.clear();
        if (workingHours != null) {
            this.workingHours.addAll(workingHours);
        }
    }
}
