package com.example.ampliar.service;

import com.example.ampliar.dto.LegalGuardianDTO;
import com.example.ampliar.dto.PatientCreateDTO;
import com.example.ampliar.dto.PatientDTO;
import com.example.ampliar.dto.PatientUpdateDTO;
import com.example.ampliar.mapper.PatientDTOMapper;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.repository.LegalGuardianRepository;
import com.example.ampliar.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public PatientDTO createPatient(PatientCreateDTO dto) {
        List<LegalGuardianModel> guardians = (dto.legalGuardianIds() == null || dto.legalGuardianIds().isEmpty())
                ? List.of()
                : legalGuardianRepository.findAllById(dto.legalGuardianIds());

        PatientModel patient = new PatientModel(
                dto.birthDate(),
                guardians,
                dto.fullName(),
                dto.cpf(),
                dto.phoneNumber()
        );

        return patientDTOMapper.apply(patientRepository.save(patient));
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientUpdateDTO dto) {
        PatientModel existing = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));

        if (dto.fullName() != null) existing.setFullName(dto.fullName());
        if (dto.cpf() != null) existing.setCpf(dto.cpf());
        if (dto.phoneNumber() != null) existing.setPhoneNumber(dto.phoneNumber());
        if (dto.birthDate() != null) existing.setBirthDate(dto.birthDate());
        if (dto.legalGuardianIds() != null && !dto.legalGuardianIds().isEmpty()) {
            List<LegalGuardianModel> guardians = legalGuardianRepository.findAllById(dto.legalGuardianIds());

            if (guardians.size() != dto.legalGuardianIds().size()) {
                throw new EntityNotFoundException("Um ou mais responsáveis legais não foram encontrados");
            }

            existing.setLegalGuardians(guardians);
        }

        return patientDTOMapper.apply(patientRepository.save(existing));
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException("Paciente não encontrado");
        }
        patientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        PatientModel patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));

        return patientDTOMapper.apply(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patientDTOMapper)
                .toList();
    }
}
