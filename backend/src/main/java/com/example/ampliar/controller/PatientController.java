package com.example.ampliar.controller;

import com.example.ampliar.dto.patient.PatientCreateDTO;
import com.example.ampliar.dto.patient.PatientDTO;
import com.example.ampliar.dto.patient.PatientUpdateDTO;
import com.example.ampliar.service.PatientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@Slf4j
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientCreateDTO patient) {
        log.info("Recebida requisição POST /patients - Criar paciente: {}", patient.fullName());
        PatientDTO result = patientService.createPatient(patient);
        log.info("Paciente criado com sucesso - ID: {}", result.id());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientUpdateDTO updatedPatient) {
        log.info("Recebida requisição PUT /patients/{} - Atualizar paciente", id);
        PatientDTO result = patientService.updatePatient(id, updatedPatient);
        log.info("Paciente atualizado com sucesso - ID: {}", id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        log.info("Recebida requisição DELETE /patients/{} - Excluir paciente", id);
        patientService.deletePatient(id);
        log.info("Paciente excluído com sucesso - ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        log.debug("Recebida requisição GET /patients/{} - Buscar paciente por ID", id);
        PatientDTO result = patientService.getPatientById(id);
        log.debug("Paciente encontrado - ID: {}", id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        log.debug("Recebida requisição GET /patients - Listar todos os pacientes");
        List<PatientDTO> result = patientService.getAllPatients();
        log.debug("Lista de pacientes retornada - Total: {}", result.size());
        return ResponseEntity.ok(result);
    }
}
