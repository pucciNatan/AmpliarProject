package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "psychologist")
public class PsychologistModel extends PersonAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
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
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("O e-mail deve ser válido");
        }
        this.email = email.trim().toLowerCase();
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("A senha deve conter no mínimo 6 caracteres");
        }
        this.password = password;
    }
}
