package com.example.ampliar.service;

import com.example.ampliar.dto.LegalGuardianDTO;
import com.example.ampliar.mapper.LegalGuardianDTOMapper;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.repository.LegalGuardianRepository;
import com.example.ampliar.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LegalGuardianService {

    private final LegalGuardianRepository legalGuardianRepository;
    private final PatientRepository patientRepository;
    private final LegalGuardianDTOMapper legalGuardianDTOMapper;

    public LegalGuardianService(
            LegalGuardianRepository legalGuardianRepository,
            PatientRepository patientRepository,
            LegalGuardianDTOMapper legalGuardianDTOMapper
    ) {
        this.legalGuardianRepository = legalGuardianRepository;
        this.patientRepository = patientRepository;
        this.legalGuardianDTOMapper = legalGuardianDTOMapper;
    }

    public LegalGuardianDTO createGuardian(LegalGuardianDTO dto) {
        List<PatientModel> patients = patientRepository.findAllById(dto.patientIds());

        LegalGuardianModel model = new LegalGuardianModel(
                patients,
                dto.fullName(),
                dto.cpf(),
                dto.phoneNumber()
        );

        return legalGuardianDTOMapper.apply(legalGuardianRepository.save(model));
    }

    public LegalGuardianDTO updateGuardian(Long id, LegalGuardianDTO dto) {
        LegalGuardianModel existing = legalGuardianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Responsável legal não encontrado"));

        existing.setFullName(dto.fullName());
        existing.setCpf(dto.cpf());
        existing.setPhoneNumber(dto.phoneNumber());
        existing.setPatients(patientRepository.findAllById(dto.patientIds()));

        return legalGuardianDTOMapper.apply(legalGuardianRepository.save(existing));
    }

    public LegalGuardianDTO getGuardianById(Long id) {
        return legalGuardianRepository.findById(id)
                .map(legalGuardianDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Responsável legal não encontrado"));
    }

    public List<LegalGuardianDTO> getAllGuardians() {
        return legalGuardianRepository.findAll()
                .stream()
                .map(legalGuardianDTOMapper)
                .collect(Collectors.toList());
    }

    public void deleteGuardian(Long id) {
        if (!legalGuardianRepository.existsById(id)) {
            throw new EntityNotFoundException("Responsável legal não encontrado");
        }
        legalGuardianRepository.deleteById(id);
    }
}
