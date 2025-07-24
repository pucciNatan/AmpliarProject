package com.example.ampliar.controller;

import com.example.ampliar.dto.PsychologistCreateDTO;
import com.example.ampliar.dto.PsychologistDTO;
import com.example.ampliar.service.PsychologistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/psychologists")
public class PsychologistController {

    @Autowired
    private PsychologistService psychologistService;

    @PutMapping("/{id}")
    public PsychologistDTO updatePsychologist(@PathVariable Long id, @RequestBody PsychologistCreateDTO dto) {
        return psychologistService.updatePsychologist(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletePsychologist(@PathVariable Long id) {
        psychologistService.deletePsychologist(id);
    }

    @GetMapping("/{id}")
    public PsychologistDTO getPsychologistById(@PathVariable Long id) {
        return psychologistService.getPsychologistById(id);
    }

    @GetMapping
    public List<PsychologistDTO> getAllPsychologists() {
        return psychologistService.getAllPsychologists();
    }
}