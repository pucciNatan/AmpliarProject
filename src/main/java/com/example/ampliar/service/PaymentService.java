package com.example.ampliar.service;

import com.example.ampliar.model.PaymentModel;
import com.example.ampliar.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentModel createPayment(PaymentModel payment) {
        return paymentRepository.save(payment);
    }

    public PaymentModel updatePayment(Long id, PaymentModel updatedPayment) {
        PaymentModel existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        existingPayment.setValor(updatedPayment.getValor());
        existingPayment.setPaymentDate(updatedPayment.getPaymentDate());
        existingPayment.setPayer(updatedPayment.getPayer());

        return paymentRepository.save(existingPayment);
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found");
        }
        paymentRepository.deleteById(id);
    }

    public PaymentModel getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
    }

    public List<PaymentModel> getAllPayments(){
        return paymentRepository.findAll();
    }
}
