package com.example.ampliar.model;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class PersonAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome completo é obrigatório")
    @Pattern(
        regexp = "^[\\p{L} .'-]+$",
        message = "O nome completo contém caracteres inválidos"
    )
    private String fullName;

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    @Column(unique = true)
    private String cpf;    
    @Pattern(
        regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$",
        message = "O número de telefone deve estar no formato (11) 91234-5678")
    private String phoneNumber;

    public PersonAbstract(String fullName, String cpf, String phoneNumber) {
        this.fullName = normalizeName(fullName);
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
    }

    public PersonAbstract(String fullName, String cpf) {
        this(fullName, cpf, null);
    }

    public void setFullName(String fullName) {
        this.fullName = normalizeName(fullName);
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome completo é obrigatório");
        }
        return name.trim();
    }
}
