package com.example.ampliar.controller;

import com.example.ampliar.dto.LegalGuardianDTO;
import com.example.ampliar.service.LegalGuardianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guardians")
public class LegalGuardianController {

    @Autowired
    private LegalGuardianService legalGuardianService;

    @PostMapping
    public LegalGuardianDTO createGuardian(@RequestBody LegalGuardianDTO guardianDTO) {
        return legalGuardianService.createGuardian(guardianDTO);
    }

    @PutMapping("/{id}")
    public LegalGuardianDTO updateGuardian(@PathVariable Long id, @RequestBody LegalGuardianDTO updatedDTO) {
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
