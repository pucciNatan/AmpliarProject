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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
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
        log.info("Criando pagamento no valor de {} para pagador ID: {}", dto.valor(), dto.payerId());
        
        try {
            PayerModel payer = getPayerOrThrow(dto.payerId());

            PaymentModel payment = new PaymentModel();
            payment.setValor(dto.valor());
            payment.setPaymentDate(dto.paymentDate());
            payment.setPayer(payer);

            PaymentDTO result = paymentDTOMapper.apply(paymentRepository.save(payment));
            log.info("Pagamento criado com sucesso ID: {}", result.id());
            return result;
            
        } catch (EntityNotFoundException e) {
            log.error("Pagador não encontrado para criar pagamento ID: {}", dto.payerId());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar pagamento", e);
            throw new RuntimeException("Erro interno ao criar pagamento", e);
        }
    }

    @Transactional
    public PaymentDTO updatePayment(Long id, PaymentUpdateDTO dto) {
        log.info("Atualizando pagamento ID: {}", id);
        
        try {
            PaymentModel existing = paymentRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Pagamento não encontrado para atualização ID: {}", id);
                        return new EntityNotFoundException("Pagamento não encontrado");
                    });

            if (dto.valor() != null) {
                existing.setValor(dto.valor());
                log.debug("Valor do pagamento atualizado para: {}", dto.valor());
            }
            if (dto.paymentDate() != null) {
                existing.setPaymentDate(dto.paymentDate());
                log.debug("Data do pagamento atualizada para: {}", dto.paymentDate());
            }
            if (dto.payerId() != null) {
                existing.setPayer(getPayerOrThrow(dto.payerId()));
                log.debug("Pagador do pagamento atualizado para ID: {}", dto.payerId());
            }

            PaymentDTO result = paymentDTOMapper.apply(paymentRepository.save(existing));
            log.info("Pagamento atualizado com sucesso ID: {}", id);
            return result;
            
        } catch (EntityNotFoundException e) {
            log.error("Pagamento não encontrado para atualização ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar pagamento ID: {}", id, e);
            throw new RuntimeException("Erro interno ao atualizar pagamento", e);
        }
    }

    @Transactional
    public void deletePayment(Long id) {
        log.info("Excluindo pagamento ID: {}", id);
        
        try {
            if (!paymentRepository.existsById(id)) {
                log.warn("Tentativa de excluir pagamento inexistente ID: {}", id);
                throw new EntityNotFoundException("Pagamento não encontrado");
            }
            
            paymentRepository.deleteById(id);
            log.info("Pagamento excluído com sucesso ID: {}", id);
            
        } catch (EntityNotFoundException e) {
            log.warn("Pagamento não encontrado para exclusão ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao excluir pagamento ID: {}", id, e);
            throw new RuntimeException("Erro interno ao excluir pagamento", e);
        }
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        log.debug("Buscando pagamento por ID: {}", id);
        
        try {
            PaymentDTO result = paymentRepository.findById(id)
                    .map(paymentDTOMapper)
                    .orElseThrow(() -> {
                        log.warn("Pagamento não encontrado ID: {}", id);
                        return new EntityNotFoundException("Pagamento não encontrado");
                    });
            log.debug("Pagamento encontrado ID: {}", id);
            return result;
            
        } catch (EntityNotFoundException e) {
            log.warn("Pagamento não encontrado na consulta ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar pagamento ID: {}", id, e);
            throw new RuntimeException("Erro interno ao buscar pagamento", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        log.debug("Buscando todos os pagamentos");
        
        try {
            List<PaymentDTO> result = paymentRepository.findAll()
                    .stream()
                    .map(paymentDTOMapper)
                    .toList();
            log.debug("Encontrados {} pagamentos", result.size());
            return result;
        } catch (Exception e) {
            log.error("Erro ao buscar todos os pagamentos", e);
            throw new RuntimeException("Erro interno ao buscar pagamentos", e);
        }
    }

    private PayerModel getPayerOrThrow(Long id) {
        log.debug("Validando existência do pagador ID: {}", id);
        return payerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Pagador não encontrado ID: {}", id);
                    return new EntityNotFoundException("Pagador não encontrado");
                });
    }
}