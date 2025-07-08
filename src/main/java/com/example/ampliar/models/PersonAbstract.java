package com.example.ampliar.models;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class PersonAbstract {
    private String fullName;
    private String cpf;
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
