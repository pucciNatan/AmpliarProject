package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
@Slf4j
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    // ✅ CORREÇÃO: Cascade delete adicionado
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PayerModel payer;

    public void setId(Long id) {
        if (id != null && id < 0) {
            log.error("Tentativa de definir ID negativo: {}", id);
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
        log.debug("ID do pagamento definido: {}", id);
    }

    public void setValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Tentativa de definir valor inválido: {}", valor);
            throw new IllegalArgumentException("O valor do pagamento deve ser maior que zero");
        }
        this.valor = valor;
        log.debug("Valor do pagamento definido: {}", valor);
    }

    public void setPaymentDate(LocalDate paymentDate) {
        if (paymentDate == null || paymentDate.isAfter(LocalDate.now())) {
            log.error("Tentativa de definir data de pagamento futura ou nula: {}", paymentDate);
            throw new IllegalArgumentException("A data do pagamento não pode ser futura");
        }
        this.paymentDate = paymentDate;
        log.debug("Data do pagamento definida: {}", paymentDate);
    }

    public void setPayer(PayerModel payer) {
        if (payer == null) {
            log.error("Tentativa de definir pagador nulo");
            throw new IllegalArgumentException("O pagador é obrigatório");
        }
        this.payer = payer;
        log.debug("Pagador definido: {}", payer.getId());
    }
}