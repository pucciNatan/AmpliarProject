package com.example.ampliar.service;

import com.example.ampliar.dto.PayerDTO;
import com.example.ampliar.mapper.PayerDTOMapper;
import com.example.ampliar.model.PayerModel;
import com.example.ampliar.repository.PayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayerService {

    private final PayerRepository payerRepository;
    private final PayerDTOMapper payerDTOMapper;

    public PayerService(PayerRepository payerRepository, PayerDTOMapper payerDTOMapper) {
        this.payerRepository = payerRepository;
        this.payerDTOMapper = payerDTOMapper;
    }

    public PayerDTO createPayer(PayerDTO dto) {
        PayerModel model = new PayerModel(dto.fullName(), dto.cpf());
        model.setPhoneNumber(dto.phoneNumber());
        return payerDTOMapper.apply(payerRepository.save(model));
    }

    public List<PayerDTO> getAllPayers() {
        return payerRepository.findAll()
                .stream()
                .map(payerDTOMapper)
                .collect(Collectors.toList());
    }

    public PayerDTO getPayerById(Long id) {
        return payerRepository.findById(id)
                .map(payerDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Pagador não encontrado"));
    }

    public void deletePayer(Long id) {
        if (!payerRepository.existsById(id)) {
            throw new EntityNotFoundException("Pagador não encontrado");
        }
        payerRepository.deleteById(id);
    }

    public PayerDTO updatePayer(Long id, PayerDTO dto) {
        PayerModel existing = payerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagador não encontrado"));

        existing.setFullName(dto.fullName());
        existing.setCpf(dto.cpf());
        existing.setPhoneNumber(dto.phoneNumber());

        return payerDTOMapper.apply(payerRepository.save(existing));
    }
}
