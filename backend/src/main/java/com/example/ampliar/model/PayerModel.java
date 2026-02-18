package com.example.ampliar.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @OneToMany(mappedBy = "payer", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PaymentModel> payments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "psychologist_id", nullable = false)
    @JsonBackReference
    private PsychologistModel psychologist;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    public PayerModel(String fullName, String cpf, String phoneNumber, PsychologistModel psychologist) {
        super(fullName, cpf, phoneNumber);
        this.psychologist = psychologist;
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

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

    public void setPsychologist(PsychologistModel psychologist) {
        this.psychologist = psychologist;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

}
