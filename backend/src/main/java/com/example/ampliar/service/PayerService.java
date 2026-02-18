package com.example.ampliar.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ampliar.dto.payer.PayerCreateDTO;
import com.example.ampliar.dto.payer.PayerDTO;
import com.example.ampliar.dto.payer.PayerUpdateDTO;
import com.example.ampliar.mapper.PayerDTOMapper;
import com.example.ampliar.model.PayerModel;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.PayerRepository;
import com.example.ampliar.repository.PsychologistRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PayerService {

    private final PayerRepository payerRepository;
    private final PayerDTOMapper payerDTOMapper;
    private final PsychologistRepository psychologistRepository;

    public PayerService(
            PayerRepository payerRepository,
            PayerDTOMapper payerDTOMapper,
            PsychologistRepository psychologistRepository
    ) {
        this.payerRepository = payerRepository;
        this.payerDTOMapper = payerDTOMapper;
        this.psychologistRepository = psychologistRepository;
    }

    private PsychologistModel getAuthenticatedPsychologist() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return psychologistRepository.findByEmailAndDeletedAtIsNull(username)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo não encontrado com email: " + username));
    }

    @Transactional
    public PayerDTO createPayer(PayerCreateDTO dto) {
        log.info("Criando pagador: {}", dto.fullName());

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            PayerModel model = new PayerModel(
                    dto.fullName(),
                    dto.cpf(),
                    dto.phoneNumber(),
                    psychologist
            );

            PayerDTO result = payerDTOMapper.apply(payerRepository.save(model));
            log.info("Pagador criado com sucesso ID: {}", result.id());
            return result;
        } catch (Exception e) {
            log.error("Erro ao criar pagador: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public PayerDTO updatePayer(Long id, PayerUpdateDTO dto) {
        log.info("Atualizando pagador ID: {}", id);

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            PayerModel existing = payerRepository.findByIdAndPsychologistAndDeletedAtIsNull(id, psychologist)
                    .orElseThrow(() -> {
                        log.error("Pagador não encontrado ID: {}", id);
                        return new EntityNotFoundException("Pagador não encontrado");
                    });

            if (dto.fullName() != null) existing.setFullName(dto.fullName());
            if (dto.cpf() != null) existing.setCpf(dto.cpf());
            if (dto.phoneNumber() != null) existing.setPhoneNumber(dto.phoneNumber());

            PayerDTO result = payerDTOMapper.apply(payerRepository.save(existing));
            log.info("Pagador atualizado com sucesso ID: {}", id);
            return result;
        } catch (Exception e) {
            log.error("Erro ao atualizar pagador ID: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public void deletePayer(Long id) {
        log.info("Iniciando exclusão do pagador ID: {}", id);

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        PayerModel payer = payerRepository.findByIdAndPsychologistAndDeletedAtIsNull(id, psychologist)
                .orElseThrow(() -> {
                    log.warn("Tentativa de excluir pagador inexistente ID: {}", id);
                    return new EntityNotFoundException("Pagador não encontrado");
                });

        try {
            payer.setDeletedAt(LocalDateTime.now());
            payerRepository.save(payer);
            log.info("Pagador excluído com sucesso ID: {}", id);

        } catch (Exception e) {
            log.error("Erro ao excluir pagador ID: {}", id, e);
            throw new RuntimeException("Erro ao excluir pagador", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PayerDTO> getAllPayers() {
        log.debug("Buscando todos os pagadores");

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            var payers = payerRepository.findAllByPsychologistAndDeletedAtIsNull(psychologist)
                    .stream()
                    .map(payerDTOMapper)
                    .toList();
            log.debug("Encontrados {} pagadores", payers.size());
            return payers;
        } catch (Exception e) {
            log.error("Erro ao buscar pagadores", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public PayerDTO getPayerById(Long id) {
        log.debug("Buscando pagador por ID: {}", id);

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            var payer = payerRepository.findByIdAndPsychologistAndDeletedAtIsNull(id, psychologist)
                    .map(payerDTOMapper)
                    .orElseThrow(() -> {
                        log.warn("Pagador não encontrado ID: {}", id);
                        return new EntityNotFoundException("Pagador não encontrado");
                    });
            log.debug("Pagador encontrado ID: {}", id);
            return payer;
        } catch (Exception e) {
            log.error("Erro ao buscar pagador ID: {}", id, e);
            throw e;
        }
    }
}
