package com.example.ampliar.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class PersonAbstract {
    private String fullName;
    private String cpf;
    private String phoneNumber;

    public PersonAbstract(String fullName, String cpf, String phoneNumber) {
        this.fullName = fullName;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
    }
}
