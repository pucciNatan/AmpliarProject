package com.example.ampliar.controller;

import com.example.ampliar.dto.payment.PaymentCreateDTO;
import com.example.ampliar.dto.payment.PaymentDTO;
import com.example.ampliar.dto.payment.PaymentUpdateDTO;
import com.example.ampliar.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentCreateDTO payment) {
        log.info("Recebida requisição POST /payments - Criar pagamento - Valor: {}, Pagador: {}", 
                 payment.valor(), payment.payerId());
        try {
            PaymentDTO result = paymentService.createPayment(payment);
            log.info("Pagamento criado com sucesso - ID: {}, Valor: {}", result.id(), result.valor());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao criar pagamento: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentUpdateDTO updatedPayment) {
        log.info("Recebida requisição PUT /payments/{} - Atualizar pagamento", id);
        try {
            PaymentDTO result = paymentService.updatePayment(id, updatedPayment);
            log.info("Pagamento atualizado com sucesso - ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao atualizar pagamento ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.info("Recebida requisição DELETE /payments/{} - Excluir pagamento", id);
        try {
            paymentService.deletePayment(id);
            log.info("Pagamento excluído com sucesso - ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao excluir pagamento ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        log.debug("🔍 Recebida requisição GET /payments/{} - Buscar pagamento por ID", id);
        try {
            PaymentDTO result = paymentService.getPaymentById(id);
            log.debug("Pagamento encontrado - ID: {}, Valor: {}", id, result.valor());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("Pagamento não encontrado - ID: {} - {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        log.debug("Recebida requisição GET /payments - Listar todos os pagamentos");
        try {
            List<PaymentDTO> result = paymentService.getAllPayments();
            log.debug("Lista de pagamentos retornada - Total: {}", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao listar pagamentos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}