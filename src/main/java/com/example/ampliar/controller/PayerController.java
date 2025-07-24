package com.example.ampliar.controller;

import com.example.ampliar.dto.PayerDTO;
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
    public PayerDTO createPayer(@RequestBody PayerDTO payer) {
        return payerService.createPayer(payer);
    }

    @GetMapping
    public List<PayerDTO> getAllPayers() {
        return payerService.getAllPayers();
    }

    @GetMapping("/{id}")
    public PayerDTO getPayerById(@PathVariable Long id) {
        return payerService.getPayerById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePayer(@PathVariable Long id) {
        payerService.deletePayer(id);
    }

    @PutMapping("/{id}")
    public PayerDTO updatePayer(@PathVariable Long id, @RequestBody PayerDTO updatedPayer) {
        return payerService.updatePayer(id, updatedPayer);
    }

}
