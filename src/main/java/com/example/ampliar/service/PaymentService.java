package com.example.ampliar.service;

import com.example.ampliar.dto.PaymentDTO;
import com.example.ampliar.mapper.PaymentDTOMapper;
import com.example.ampliar.model.PayerModel;
import com.example.ampliar.model.PaymentModel;
import com.example.ampliar.repository.PayerRepository;
import com.example.ampliar.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayerRepository payerRepository;
    private final PaymentDTOMapper paymentDTOMapper;

    public PaymentService(PaymentRepository paymentRepository, PayerRepository payerRepository, PaymentDTOMapper paymentDTOMapper) {
        this.paymentRepository = paymentRepository;
        this.payerRepository = payerRepository;
        this.paymentDTOMapper = paymentDTOMapper;
    }

    public PaymentDTO createPayment(PaymentDTO dto) {
        PayerModel payer = getPayer(dto.payerId());

        PaymentModel payment = new PaymentModel();
        payment.setValor(dto.valor());
        payment.setPaymentDate(dto.paymentDate());
        payment.setPayer(payer);

        return paymentDTOMapper.apply(paymentRepository.save(payment));
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO dto) {
        PaymentModel existing = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));

        existing.setValor(dto.valor());
        existing.setPaymentDate(dto.paymentDate());
        existing.setPayer(getPayer(dto.payerId()));

        return paymentDTOMapper.apply(paymentRepository.save(existing));
    }

    public PaymentDTO getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(paymentDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentDTOMapper)
                .collect(Collectors.toList());
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Pagamento não encontrado");
        }
        paymentRepository.deleteById(id);
    }

    private PayerModel getPayer(Long id) {
        return payerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagador não encontrado"));
    }
}
