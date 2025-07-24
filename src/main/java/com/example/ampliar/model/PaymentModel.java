package com.example.ampliar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private BigDecimal valor;

    @NotBlank
    private LocalDate paymentDate;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "payer_id")
    private PayerModel payer;

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

    public void setValor(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O valor do pagamento é obrigatório");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor do pagamento não pode ser negativo");
        }
        this.valor = valor;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        if (paymentDate == null) {
            throw new IllegalArgumentException("A data do pagamento é obrigatória");
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data do pagamento não pode ser futura");
        }
        this.paymentDate = paymentDate;
    }

    public void setPayer(PayerModel payer) {
        if (payer == null) {
            throw new IllegalArgumentException("O pagador é obrigatório");
        }
        this.payer = payer;
    }
}
