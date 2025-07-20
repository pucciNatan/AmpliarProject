package com.example.ampliar.service;

import com.example.ampliar.model.PayerModel;
import com.example.ampliar.repository.PayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayerService {

    @Autowired
    private PayerRepository payerRepository;

    public PayerModel createPayer(PayerModel payer) {
        return payerRepository.save(payer);
    }

    public List<PayerModel> getAllPayers() {
        return payerRepository.findAll();
    }

    public PayerModel getPayerById(Long id) {
        return payerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payer not found"));
    }

    public void deletePayer(Long id) {
        if (!payerRepository.existsById(id)) {
            throw new EntityNotFoundException("Payer not found");
        }
        payerRepository.deleteById(id);
    }

    public PayerModel updatePayer(Long id, PayerModel updatedPayer) {
        PayerModel existing = payerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payer not found"));

        existing.setFullName(updatedPayer.getFullName());
        existing.setCpf(updatedPayer.getCpf());

        return payerRepository.save(existing);
    }
}
