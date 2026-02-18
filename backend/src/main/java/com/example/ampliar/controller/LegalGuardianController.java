package com.example.ampliar.controller;

import com.example.ampliar.dto.legalGuardian.LegalGuardianCreateDTO;
import com.example.ampliar.dto.legalGuardian.LegalGuardianDTO;
import com.example.ampliar.dto.legalGuardian.LegalGuardianUpdateDTO;
import com.example.ampliar.service.LegalGuardianService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guardians")
@Slf4j
public class LegalGuardianController {

    private final LegalGuardianService legalGuardianService;

    public LegalGuardianController(LegalGuardianService legalGuardianService) {
        this.legalGuardianService = legalGuardianService;
    }

    @PostMapping
    public ResponseEntity<LegalGuardianDTO> createGuardian(@Valid @RequestBody LegalGuardianCreateDTO guardianDTO) {
        log.info("Recebida requisição POST /guardians - Criar responsável legal: {}", guardianDTO.fullName());
        LegalGuardianDTO result = legalGuardianService.createGuardian(guardianDTO);
        log.info("Responsável legal criado com sucesso - ID: {}", result.id());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalGuardianDTO> updateGuardian(@PathVariable Long id, @Valid @RequestBody LegalGuardianUpdateDTO updatedDTO) {
        log.info("Recebida requisição PUT /guardians/{} - Atualizar responsável legal", id);
        LegalGuardianDTO result = legalGuardianService.updateGuardian(id, updatedDTO);
        log.info("Responsável legal atualizado com sucesso - ID: {}", id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalGuardianDTO> getGuardianById(@PathVariable Long id) {
        log.debug("Recebida requisição GET /guardians/{} - Buscar responsável legal por ID", id);
        LegalGuardianDTO result = legalGuardianService.getGuardianById(id);
        log.debug("Responsável legal encontrado - ID: {}", id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<LegalGuardianDTO>> getAllGuardians() {
        log.debug("Recebida requisição GET /guardians - Listar todos os responsáveis legais");
        List<LegalGuardianDTO> result = legalGuardianService.getAllGuardians();
        log.debug("Lista de responsáveis legais retornada - Total: {}", result.size());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuardian(@PathVariable Long id) {
        log.info("Recebida requisição DELETE /guardians/{} - Excluir responsável legal", id);
        legalGuardianService.deleteGuardian(id);
        log.info("Responsável legal excluído com sucesso - ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
