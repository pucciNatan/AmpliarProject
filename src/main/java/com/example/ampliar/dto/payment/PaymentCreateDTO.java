package com.example.ampliar.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PaymentCreateDTO(
        @NotNull(message = "O valor para pagamento não pode ser nulo.")
        @PositiveOrZero(message = "O valor do pagamento não pode ser negativo.")
        BigDecimal valor,

        @NotNull(message = "A data de pagamento não pode ser nula.")
        LocalDate paymentDate,
        
        @NotNull(message = "O pagador não pode ser nulo.")
        Long payerId
) {}

