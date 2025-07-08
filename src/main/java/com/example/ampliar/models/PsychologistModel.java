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
    private String email;
    private String password;

    public PsychologistModel(String fullName, String cpf, String phoneNumber, String email, String password){
        super(fullName, cpf, phoneNumber);
        this.email = email;
        this.password = password;
    }
}
