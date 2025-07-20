package com.example.ampliar.controller;

import com.example.ampliar.model.LegalGuardianModel;
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
    public LegalGuardianModel createGuardian(@RequestBody LegalGuardianModel guardian) {
        return legalGuardianService.createGuardian(guardian);
    }

    @PutMapping("/{id}")
    public LegalGuardianModel updateGuardian(@PathVariable Long id, @RequestBody LegalGuardianModel updatedGuardian) {
        return legalGuardianService.updateGuardian(id, updatedGuardian);
    }

    @DeleteMapping("/{id}")
    public void deleteGuardian(@PathVariable Long id) {
        legalGuardianService.deleteGuardian(id);
    }

    @GetMapping("/{id}")
    public LegalGuardianModel getGuardianById(@PathVariable Long id) {
        return legalGuardianService.getGuardianById(id);
    }

    @GetMapping
    public List<LegalGuardianModel> getAllGuardians() {
        return legalGuardianService.getAllGuardians();
    }
}
