package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "payer")
public class PayerModel extends PersonAbstract {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    public PayerModel(String fullName, String cpf) {
        super(fullName, cpf);
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

}
