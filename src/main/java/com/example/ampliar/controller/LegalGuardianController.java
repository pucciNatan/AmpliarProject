package com.example.ampliar.controller;

import com.example.ampliar.dto.legalGuardian.LegalGuardianCreateDTO;
import com.example.ampliar.dto.legalGuardian.LegalGuardianDTO;
import com.example.ampliar.dto.legalGuardian.LegalGuardianUpdateDTO;
import com.example.ampliar.service.LegalGuardianService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guardians")
public class LegalGuardianController {

    private final LegalGuardianService legalGuardianService;

    public LegalGuardianController(LegalGuardianService legalGuardianService) {
        this.legalGuardianService = legalGuardianService;
    }

    @PostMapping
    public LegalGuardianDTO createGuardian(@Valid @RequestBody LegalGuardianCreateDTO guardianDTO) {
        return legalGuardianService.createGuardian(guardianDTO);
    }

    @PutMapping("/{id}")
    public LegalGuardianDTO updateGuardian(@PathVariable Long id, @Valid @RequestBody LegalGuardianUpdateDTO updatedDTO) {
        return legalGuardianService.updateGuardian(id, updatedDTO);
    }

    @GetMapping("/{id}")
    public LegalGuardianDTO getGuardianById(@PathVariable Long id) {
        return legalGuardianService.getGuardianById(id);
    }

    @GetMapping
    public List<LegalGuardianDTO> getAllGuardians() {
        return legalGuardianService.getAllGuardians();
    }

    @DeleteMapping("/{id}")
    public void deleteGuardian(@PathVariable Long id) {
        legalGuardianService.deleteGuardian(id);
    }
}
