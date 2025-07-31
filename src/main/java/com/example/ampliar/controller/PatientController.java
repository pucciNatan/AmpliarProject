package com.example.ampliar.controller;

import com.example.ampliar.dto.PatientCreateDTO;
import com.example.ampliar.dto.PatientDTO;
import com.example.ampliar.dto.PatientUpdateDTO;
import com.example.ampliar.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public PatientDTO createPatient(@Valid @RequestBody PatientCreateDTO patient) {
        return patientService.createPatient(patient);
    }

    @PutMapping("/{id}")
    public PatientDTO updatePatient(@PathVariable Long id, @Valid @RequestBody PatientUpdateDTO updatedPatient) {
        return patientService.updatePatient(id, updatedPatient);
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }

    @GetMapping("/{id}")
    public PatientDTO getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientService.getAllPatients();
    }
}
