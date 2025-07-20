package com.example.ampliar.controller;

import com.example.ampliar.DTO.PatientDTO;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    public PatientModel createPatient(@RequestBody PatientDTO patient) {
        return patientService.createPatient(patient);
    }

    @PutMapping("/{id}")
    public PatientModel updatePatient(@PathVariable Long id, @RequestBody PatientModel updatedPatient) {
        return patientService.updatePatient(id, updatedPatient);
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }

    @GetMapping("/{id}")
    public PatientModel getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping
    public List<PatientModel> getAllPatients() {
        return patientService.getAllPatients();
    }
}
