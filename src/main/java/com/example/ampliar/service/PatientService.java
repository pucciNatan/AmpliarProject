package com.example.ampliar.service;

import com.example.ampliar.models.PatientModel;
import com.example.ampliar.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public PatientModel createPatient(PatientModel patient) {
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
