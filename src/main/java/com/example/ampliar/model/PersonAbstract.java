package com.example.ampliar.model;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class PersonAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z][\\p{L} .'-]*$")
    private String fullName;
    
    @NotBlank
    @Column(unique = true)
    @CPF
    private String cpf;
    
    @Pattern(regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$")
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
