package com.example.ampliar.model;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
        this.fullName = fullName;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
    }

    public PersonAbstract(String fullName, String cpf) {
        this.fullName = fullName;
        this.cpf = cpf;
    }
}
