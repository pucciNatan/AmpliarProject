package com.example.ampliar.service;

import com.example.ampliar.dto.PayerCreateDTO;
import com.example.ampliar.dto.PayerDTO;
import com.example.ampliar.dto.PayerUpdateDTO;
import com.example.ampliar.mapper.PayerDTOMapper;
import com.example.ampliar.model.PayerModel;
import com.example.ampliar.repository.PayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PayerService {

    private final PayerRepository payerRepository;
    private final PayerDTOMapper payerDTOMapper;

    public PayerService(PayerRepository payerRepository, PayerDTOMapper payerDTOMapper) {
        this.payerRepository = payerRepository;
        this.payerDTOMapper = payerDTOMapper;
    }

    @Transactional
    public PayerDTO createPayer(PayerCreateDTO dto) {
        PayerModel model = new PayerModel(
                dto.fullName(),
                dto.cpf()
        );
        model.setPhoneNumber(dto.phoneNumber());

        return payerDTOMapper.apply(payerRepository.save(model));
    }

    @Transactional
    public PayerDTO updatePayer(Long id, PayerUpdateDTO dto) {
        PayerModel existing = payerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagador não encontrado"));

        if (dto.fullName() != null) existing.setFullName(dto.fullName());
        if (dto.cpf() != null) existing.setCpf(dto.cpf());
        if (dto.phoneNumber() != null) existing.setPhoneNumber(dto.phoneNumber());

        return payerDTOMapper.apply(payerRepository.save(existing));
    }

    @Transactional
    public void deletePayer(Long id) {
        if (!payerRepository.existsById(id)) {
            throw new EntityNotFoundException("Pagador não encontrado");
        }
        payerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PayerDTO> getAllPayers() {
        return payerRepository.findAll()
                .stream()
                .map(payerDTOMapper)
                .toList();
    }

    @Transactional(readOnly = true)
    public PayerDTO getPayerById(Long id) {
        return payerRepository.findById(id)
                .map(payerDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Pagador não encontrado"));
    }
}
