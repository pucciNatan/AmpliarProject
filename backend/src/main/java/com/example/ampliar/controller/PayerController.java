package com.example.ampliar.controller;

import com.example.ampliar.dto.payer.PayerCreateDTO;
import com.example.ampliar.dto.payer.PayerDTO;
import com.example.ampliar.dto.payer.PayerUpdateDTO;
import com.example.ampliar.service.PayerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payers")
@Slf4j
public class PayerController {

    private final PayerService payerService;

    public PayerController(PayerService payerService){
        this.payerService = payerService;
    }

    @PostMapping
    public ResponseEntity<PayerDTO> createPayer(@Valid @RequestBody PayerCreateDTO payer) {
        log.info("Recebida requisição POST /payers - Criar pagador: {}", payer.fullName());
        try {
            PayerDTO result = payerService.createPayer(payer);
            log.info("Pagador criado com sucesso - ID: {}, Nome: {}", result.id(), result.fullName());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao criar pagador: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayerDTO> updatePayer(@PathVariable Long id, @Valid @RequestBody PayerUpdateDTO updatedPayer) {
        log.info("Recebida requisição PUT /payers/{} - Atualizar pagador", id);
        try {
            PayerDTO result = payerService.updatePayer(id, updatedPayer);
            log.info("Pagador atualizado com sucesso - ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao atualizar pagador ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PayerDTO>> getAllPayers() {
        log.debug("🔍 Recebida requisição GET /payers - Listar todos os pagadores");
        try {
            List<PayerDTO> result = payerService.getAllPayers();
            log.debug("Lista de pagadores retornada - Total: {}", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao listar pagadores: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayerDTO> getPayerById(@PathVariable Long id) {
        log.debug("Recebida requisição GET /payers/{} - Buscar pagador por ID", id);
        try {
            PayerDTO result = payerService.getPayerById(id);
            log.debug("Pagador encontrado - ID: {}, Nome: {}", id, result.fullName());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("Pagador não encontrado - ID: {} - {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayer(@PathVariable Long id) {
        log.info("Recebida requisição DELETE /payers/{} - Excluir pagador", id);
        try {
            payerService.deletePayer(id);
            log.info("Pagador excluído com sucesso - ID: {} (pagamentos associados também excluídos)", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao excluir pagador ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}