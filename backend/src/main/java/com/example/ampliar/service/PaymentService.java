package com.example.ampliar.service;

import com.example.ampliar.dto.payment.PaymentCreateDTO;
import com.example.ampliar.dto.payment.PaymentDTO;
import com.example.ampliar.dto.payment.PaymentUpdateDTO;
import com.example.ampliar.mapper.PaymentDTOMapper;
import com.example.ampliar.model.PayerModel;
import com.example.ampliar.model.PaymentModel;
import com.example.ampliar.repository.PayerRepository;
import com.example.ampliar.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayerRepository payerRepository;
    private final PaymentDTOMapper paymentDTOMapper;

    public PaymentService(
            PaymentRepository paymentRepository,
            PayerRepository payerRepository,
            PaymentDTOMapper paymentDTOMapper
    ) {
        this.paymentRepository = paymentRepository;
        this.payerRepository = payerRepository;
        this.paymentDTOMapper = paymentDTOMapper;
    }

    @Transactional
    public PaymentDTO createPayment(PaymentCreateDTO dto) {
        PayerModel payer = getPayerOrThrow(dto.payerId());

        PaymentModel payment = new PaymentModel();
        payment.setValor(dto.valor());
        payment.setPaymentDate(dto.paymentDate());
        payment.setPayer(payer);

        return paymentDTOMapper.apply(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentDTO updatePayment(Long id, PaymentUpdateDTO dto) {
        PaymentModel existing = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));

        if (dto.valor() != null) existing.setValor(dto.valor());
        if (dto.paymentDate() != null) existing.setPaymentDate(dto.paymentDate());
        if (dto.payerId() != null) {
            existing.setPayer(getPayerOrThrow(dto.payerId()));
        }

        return paymentDTOMapper.apply(paymentRepository.save(existing));
    }

    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Pagamento não encontrado");
        }
        paymentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(paymentDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentDTOMapper)
                .toList();
    }

    private PayerModel getPayerOrThrow(Long id) {
        return payerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagador não encontrado"));
    }
}
