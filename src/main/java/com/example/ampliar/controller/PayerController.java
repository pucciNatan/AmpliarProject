package com.example.ampliar.controller;

import com.example.ampliar.dto.PayerCreateDTO;
import com.example.ampliar.dto.PayerDTO;
import com.example.ampliar.dto.PayerUpdateDTO;
import com.example.ampliar.service.PayerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payers")
public class PayerController {

    private final PayerService payerService;

    public PayerController(PayerService payerService){
        this.payerService = payerService;
    }

    @PostMapping
    public PayerDTO createPayer(@Valid @RequestBody PayerCreateDTO payer) {
        return payerService.createPayer(payer);
    }

    @PutMapping("/{id}")
    public PayerDTO updatePayer(@PathVariable Long id, @Valid @RequestBody PayerUpdateDTO updatedPayer) {
        return payerService.updatePayer(id, updatedPayer);
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

}
