package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "payer")
public class PayerModel extends PersonAbstract {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    // ✅ CORREÇÃO: Relacionamento com cascade delete
    @OneToMany(mappedBy = "payer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentModel> payments = new ArrayList<>();

    public PayerModel(String fullName, String cpf, String phoneNumber) {
        super(fullName, cpf, phoneNumber);
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

    // ✅ CORREÇÃO: Métodos para gerenciar relacionamento bidirecional
    public void addPayment(PaymentModel payment) {
        payments.add(payment);
        payment.setPayer(this);
    }

    public void removePayment(PaymentModel payment) {
        payments.remove(payment);
        payment.setPayer(null);
    }
    
    public void clearPayments() {
        payments.clear();
    }
}