package com.example.ampliar.service;

import com.example.ampliar.DTO.PatientDTO;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.repository.LegalGuardianRepository;
import com.example.ampliar.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final LegalGuardianRepository legalGuardianRepository;

    public PatientService(PatientRepository patientRepository, LegalGuardianRepository legalGuardianRepository) {
        this.patientRepository = patientRepository;
        this.legalGuardianRepository = legalGuardianRepository;
    }

    public PatientModel createPatient(PatientDTO dto) {
        var guardians = legalGuardianRepository.findAllById(dto.getLegalGuardianIds());

        PatientModel patient = new PatientModel(
                dto.getBirthDate(),
                guardians,
                dto.getFullName(),
                dto.getCpf(),
                dto.getPhoneNumber()
        );

        return patientRepository.save(patient);
    }

    public PatientModel updatePatient(Long id, PatientModel updatedPatient) {
        PatientModel existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        existingPatient.setFullName(updatedPatient.getFullName());
        existingPatient.setCpf(updatedPatient.getCpf());
        existingPatient.setPhoneNumber(updatedPatient.getPhoneNumber());
        existingPatient.setBirthDate(updatedPatient.getBirthDate());
        existingPatient.setLegalGuardians(updatedPatient.getLegalGuardians());

        return patientRepository.save(existingPatient);
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    public PatientModel getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }

    public List<PatientModel> getAllPatients() {
        return patientRepository.findAll();
    }


}
