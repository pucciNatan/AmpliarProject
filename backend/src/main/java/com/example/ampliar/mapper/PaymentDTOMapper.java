package com.example.ampliar.mapper;

import com.example.ampliar.dto.payment.PaymentDTO;
import com.example.ampliar.model.PaymentModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PaymentDTOMapper implements Function<PaymentModel, PaymentDTO> {

    @Override
    public PaymentDTO apply(PaymentModel payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getValor(),
                payment.getPaymentDate(),
                payment.getPayer() != null ? payment.getPayer().getId() : null
        );
    }
}
