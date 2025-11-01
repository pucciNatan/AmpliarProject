package com.example.ampliar.controller;

import com.example.ampliar.dto.psychologist.PsychologistCreateDTO;
import com.example.ampliar.dto.psychologist.PsychologistDTO;
import com.example.ampliar.dto.psychologist.PsychologistUpdateDTO;
import com.example.ampliar.service.PsychologistService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/psychologists")
@Slf4j
public class PsychologistController {

    private final PsychologistService psychologistService;

    public PsychologistController(PsychologistService psychologistService){
        this.psychologistService = psychologistService;
    }

    // ✅ CORREÇÃO: Adicionado endpoint CREATE
    @PostMapping
    public PsychologistDTO createPsychologist(@Valid @RequestBody PsychologistCreateDTO dto) {
        log.info("Criando psicólogo: {}", dto.email());
        return psychologistService.createPsychologist(dto);
    }

    @PutMapping("/{id}")
    public PsychologistDTO updatePsychologist(@PathVariable Long id, @Valid @RequestBody PsychologistUpdateDTO dto) {
        log.info("Atualizando psicólogo ID: {}", id);
        return psychologistService.updatePsychologist(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletePsychologist(@PathVariable Long id) {
        log.info("Excluindo psicólogo ID: {}", id);
        psychologistService.deletePsychologist(id);
    }

    @GetMapping("/{id}")
    public PsychologistDTO getPsychologistById(@PathVariable Long id) {
        log.debug("Buscando psicólogo por ID: {}", id);
        return psychologistService.getPsychologistById(id);
    }

    @GetMapping
    public List<PsychologistDTO> getAllPsychologists() {
        log.debug("Buscando todos os psicólogos");
        return psychologistService.getAllPsychologists();
    }
}