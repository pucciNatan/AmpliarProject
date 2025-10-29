package com.example.ampliar.dto.payment;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentUpdateDTO(
        @PositiveOrZero(message = "O valor do pagamento não pode ser negativo.")
        BigDecimal valor,

        LocalDate paymentDate,
        
        Long payerId
) {}

