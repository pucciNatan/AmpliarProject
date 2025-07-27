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

    public PayerModel(String fullName, String cpf) {
        super(fullName, cpf);
    }
}
