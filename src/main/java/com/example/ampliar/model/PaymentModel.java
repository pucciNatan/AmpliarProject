package com.example.ampliar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class PaymentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @NotBlank
    BigDecimal valor;

    @NotBlank
    LocalDate paymentDate;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "payer_id")
    PayerModel payer;
}
