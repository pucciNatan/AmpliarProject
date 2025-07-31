package com.example.ampliar.controller;

import com.example.ampliar.dto.payment.PaymentCreateDTO;
import com.example.ampliar.dto.payment.PaymentDTO;
import com.example.ampliar.dto.payment.PaymentUpdateDTO;
import com.example.ampliar.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentDTO createPayment(@Valid @RequestBody PaymentCreateDTO payment) {
        return paymentService.createPayment(payment);
    }

    @PutMapping("/{id}")
    public PaymentDTO updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentUpdateDTO updatedPayment) {
        return paymentService.updatePayment(id, updatedPayment);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }

    @GetMapping("/{id}")
    public PaymentDTO getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping
    public List<PaymentDTO> getAllPayments(){
        return paymentService.getAllPayments();
    }
}
