package com.example.ampliar.mapper;

import com.example.ampliar.dto.PayerDTO;
import com.example.ampliar.model.PayerModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PayerDTOMapper implements Function<PayerModel, PayerDTO> {

    @Override
    public PayerDTO apply(PayerModel payer) {
        return new PayerDTO(
                payer.getId(),
                payer.getFullName(),
                payer.getCpf(),
                payer.getPhoneNumber()
        );
    }
}
