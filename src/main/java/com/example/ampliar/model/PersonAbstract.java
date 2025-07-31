package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class PersonAbstract {

    @Column(nullable = false, length = 100)
    protected String fullName;

    @Column(nullable = false, unique = true, length = 11)
    protected String cpf;

    @Column(nullable = false, length = 15)
    protected String phoneNumber;

    protected PersonAbstract(String fullName, String cpf, String phoneNumber) {
        setFullName(fullName);
        setCpf(cpf);
        setPhoneNumber(phoneNumber);
    }

    public PersonAbstract(String fullName, String cpf) {
        this(fullName, cpf, null);
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome completo é obrigatório");
        }
        return name.trim();
    }

    public void setFullName(String fullName) {
        this.fullName = normalizeName(fullName);
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("O CPF é obrigatório");
        }
        if (!cpf.matches("^\\d{11}$")) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos");
        }
        this.cpf = cpf.trim();
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("O número de telefone é obrigatório");
        }
        if (!phoneNumber.matches("^\\(\\d{2}\\) \\d{4,5}-\\d{4}$")) {
            throw new IllegalArgumentException("O número de telefone deve estar no formato (11) 91234-5678");
        }
        this.phoneNumber = phoneNumber.trim();
    }
}
