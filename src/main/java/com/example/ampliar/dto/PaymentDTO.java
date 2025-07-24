package com.example.ampliar.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentDTO(
        Long id,
        BigDecimal valor,
        LocalDate paymentDate,
        Long payerId
) {}

