package com.example.ampliar.controller;

import com.example.ampliar.model.PayerModel;
import com.example.ampliar.service.PayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payers")
public class PayerController {

    @Autowired
    private PayerService payerService;

    @PostMapping
    public PayerModel createPayer(@RequestBody PayerModel payer) {
        return payerService.createPayer(payer);
    }

    @GetMapping
    public List<PayerModel> getAllPayers() {
        return payerService.getAllPayers();
    }

    @GetMapping("/{id}")
    public PayerModel getPayerById(@PathVariable Long id) {
        return payerService.getPayerById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePayer(@PathVariable Long id) {
        payerService.deletePayer(id);
    }

    @PutMapping("/{id}")
    public PayerModel updatePayer(@PathVariable Long id, @RequestBody PayerModel updatedPayer) {
        return payerService.updatePayer(id, updatedPayer);
    }

}
