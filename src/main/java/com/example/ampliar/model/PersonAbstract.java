package com.example.ampliar.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class PersonAbstract {

    private String fullName;
    private String cpf;
    private String phoneNumber;

    public PersonAbstract(String fullName, String cpf, String phoneNumber) {
        setFullName(fullName);
        setCpf(cpf);
        setPhoneNumber(phoneNumber);
    }

    public PersonAbstract(String fullName, String cpf) {
        setFullName(fullName);
        setCpf(cpf);
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome completo é obrigatório");
        }
        this.fullName = fullName.trim();
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("O CPF é obrigatório");
        }
        String digits = cpf.replaceAll("[^0-9]", "");
        if (digits.length() != 11) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos");
        }
        this.cpf = digits;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            String digits = phoneNumber.replaceAll("[^0-9]", "");
            if (digits.length() < 10 || digits.length() > 14) {
                throw new IllegalArgumentException("O telefone deve conter entre 10 e 14 dígitos numéricos");
            }
            this.phoneNumber = digits;
        } else {
            this.phoneNumber = null; // é opcional
        }
    }
}
