package com.example.ampliar.controller;

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
    public PaymentModel createPayment(@RequestBody PaymentModel payment) {
        return paymentService.createPayment(payment);
    }

    @PutMapping("/{id}")
    public PaymentModel updatePayment(@PathVariable Long id, @RequestBody PaymentModel updatedPayment) {
        return paymentService.updatePayment(id, updatedPayment);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }

    @GetMapping("/{id}")
    public PaymentModel getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping
    public List<PaymentModel> getAllPayments(){
        return paymentService.getAllPayments();
    }
}
