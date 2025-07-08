package com.example.ampliar.controller;

import com.example.ampliar.models.PsychologistModel;
import com.example.ampliar.service.PsychologistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/psychologists")
public class PsychologistController {

    @Autowired
    private PsychologistService psychologistService;

    @PostMapping
    public PsychologistModel createPsychologist(@RequestBody PsychologistModel psychologist) {
        return psychologistService.createPsychologist(psychologist);
    }

    @PutMapping("/{id}")
    public PsychologistModel updatePsychologist(@PathVariable Long id, @RequestBody PsychologistModel updatedPsychologist) {
        return psychologistService.updatePsychologist(id, updatedPsychologist);
    }

    @DeleteMapping("/{id}")
    public void deletePsychologist(@PathVariable Long id) {
        psychologistService.deletePsychologist(id);
    }

    @GetMapping("/{id}")
    public PsychologistModel getPsychologistById(@PathVariable Long id) {
        return psychologistService.getPsychologistById(id);
    }

    @GetMapping
    public List<PsychologistModel> getAllPsychologists() {
        return psychologistService.getAllPsychologists();
    }
}
