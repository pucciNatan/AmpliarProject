package com.example.ampliar.service;

import com.example.ampliar.dto.PatientDTO;
import com.example.ampliar.mapper.PatientDTOMapper;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.repository.LegalGuardianRepository;
import com.example.ampliar.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final LegalGuardianRepository legalGuardianRepository;
    private final PatientDTOMapper patientDTOMapper;

    public PatientService(
            PatientRepository patientRepository,
            LegalGuardianRepository legalGuardianRepository,
            PatientDTOMapper patientDTOMapper
    ) {
        this.patientRepository = patientRepository;
        this.legalGuardianRepository = legalGuardianRepository;
        this.patientDTOMapper = patientDTOMapper;
    }

    public PatientDTO createPatient(PatientDTO dto) {
        var guardians = legalGuardianRepository.findAllById(dto.legalGuardianIds());

        PatientModel patient = new PatientModel(
                dto.birthDate(),
                guardians,
                dto.fullName(),
                dto.cpf(),
                dto.phoneNumber()
        );

        return patientDTOMapper.apply(patientRepository.save(patient));
    }

    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        PatientModel existing = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));

        existing.setFullName(dto.fullName());
        existing.setCpf(dto.cpf());
        existing.setPhoneNumber(dto.phoneNumber());
        existing.setBirthDate(dto.birthDate());
        existing.setLegalGuardians(legalGuardianRepository.findAllById(dto.legalGuardianIds()));

        return patientDTOMapper.apply(patientRepository.save(existing));
    }

    public PatientDTO getPatientById(Long id) {
        PatientModel patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));

        return patientDTOMapper.apply(patient);
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patientDTOMapper)
                .toList();
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Paciente não encontrado");
        }
        patientRepository.deleteById(id);
    }
}
