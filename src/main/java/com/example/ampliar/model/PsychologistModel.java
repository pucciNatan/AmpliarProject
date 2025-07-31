package com.example.ampliar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Email
    @NotBlank
    private String email;
    

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,}$",
        message = "A senha deve conter letras e números"
    )
    private String password;

    public PsychologistModel(String fullName, String cpf, String phoneNumber, String email, String password) {
        super(fullName, cpf, phoneNumber);
        this.setEmail(email);
        this.setPassword(password);
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
