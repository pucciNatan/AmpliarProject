package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "psychologist")
public class PsychologistModel extends PersonAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;

    public PsychologistModel(String fullName, String cpf, String phoneNumber, String email, String password) {
        super(fullName, cpf, phoneNumber);
        setEmail(email);
        setPassword(password);
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail é obrigatório");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Formato de e-mail inválido");
        }
        this.email = email.trim().toLowerCase();
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("A senha deve conter no mínimo 5 caracteres");
        }
        this.password = password;
    }
}
