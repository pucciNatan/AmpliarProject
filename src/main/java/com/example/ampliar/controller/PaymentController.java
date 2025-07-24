package com.example.ampliar.controller;

import com.example.ampliar.dto.PaymentDTO;
import com.example.ampliar.model.PaymentModel;
import com.example.ampliar.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public PaymentDTO createPayment(@RequestBody PaymentDTO payment) {
        return paymentService.createPayment(payment);
    }

    @PutMapping("/{id}")
    public PaymentDTO updatePayment(@PathVariable Long id, @RequestBody PaymentDTO updatedPayment) {
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
