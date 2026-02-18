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
        PayerDTO result = payerService.createPayer(payer);
        log.info("Pagador criado com sucesso - ID: {}, Nome: {}", result.id(), result.fullName());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayerDTO> updatePayer(@PathVariable Long id, @Valid @RequestBody PayerUpdateDTO updatedPayer) {
        log.info("Recebida requisição PUT /payers/{} - Atualizar pagador", id);
        PayerDTO result = payerService.updatePayer(id, updatedPayer);
        log.info("Pagador atualizado com sucesso - ID: {}", id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<PayerDTO>> getAllPayers() {
        log.debug("Recebida requisição GET /payers - Listar todos os pagadores");
        List<PayerDTO> result = payerService.getAllPayers();
        log.debug("Lista de pagadores retornada - Total: {}", result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayerDTO> getPayerById(@PathVariable Long id) {
        log.debug("Recebida requisição GET /payers/{} - Buscar pagador por ID", id);
        PayerDTO result = payerService.getPayerById(id);
        log.debug("Pagador encontrado - ID: {}, Nome: {}", id, result.fullName());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayer(@PathVariable Long id) {
        log.info("Recebida requisição DELETE /payers/{} - Excluir pagador", id);
        payerService.deletePayer(id);
        log.info("Pagador excluído com sucesso - ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
