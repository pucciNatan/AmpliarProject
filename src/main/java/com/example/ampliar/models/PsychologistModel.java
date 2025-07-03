package com.example.ampliar.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "psychologist")
public class PsychologistModel extends PersonAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String crp;

    //TODO: Senha, como vai funcionar? Google Authenticator? Senha digitada? Como recuperar a senha?

    public PsychologistModel(String fullName, String cpf, String phoneNumber, String crp){
        super(fullName, cpf, phoneNumber);
        this.crp = crp;
    }
}
