package com.example.ampliar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "psychologist")
public class PsychologistModel extends PersonAbstract {
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;

    public PsychologistModel(String fullName, String cpf, String phoneNumber, String email, String password){
        super(fullName, cpf, phoneNumber);
        this.email = email;
        this.password = password;
    }
}
