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
        PaymentDTO result = paymentService.createPayment(payment);
        log.info("Pagamento criado com sucesso - ID: {}, Valor: {}", result.id(), result.valor());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentUpdateDTO updatedPayment) {
        log.info("Recebida requisição PUT /payments/{} - Atualizar pagamento", id);
        PaymentDTO result = paymentService.updatePayment(id, updatedPayment);
        log.info("Pagamento atualizado com sucesso - ID: {}", id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.info("Recebida requisição DELETE /payments/{} - Excluir pagamento", id);
        paymentService.deletePayment(id);
        log.info("Pagamento excluído com sucesso - ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        log.debug("Recebida requisição GET /payments/{} - Buscar pagamento por ID", id);
        PaymentDTO result = paymentService.getPaymentById(id);
        log.debug("Pagamento encontrado - ID: {}, Valor: {}", id, result.valor());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        log.debug("Recebida requisição GET /payments - Listar todos os pagamentos");
        List<PaymentDTO> result = paymentService.getAllPayments();
        log.debug("Lista de pagamentos retornada - Total: {}", result.size());
        return ResponseEntity.ok(result);
    }
}
