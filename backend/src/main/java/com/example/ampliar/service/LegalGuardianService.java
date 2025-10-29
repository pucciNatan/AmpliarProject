package com.example.ampliar.service;

import com.example.ampliar.dto.legalGuardian.LegalGuardianCreateDTO;
import com.example.ampliar.dto.legalGuardian.LegalGuardianDTO;
import com.example.ampliar.dto.legalGuardian.LegalGuardianUpdateDTO;
import com.example.ampliar.mapper.LegalGuardianDTOMapper;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.repository.LegalGuardianRepository;
import com.example.ampliar.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public LegalGuardianDTO createGuardian(LegalGuardianCreateDTO dto) {
        List<Long> requestedIds = dto.patientIds();
        List<PatientModel> patients = patientRepository.findAllById(requestedIds);

        if (patients.size() != requestedIds.size()) {
            throw new IllegalArgumentException("Um ou mais pacientes informados não existem");
        }

        LegalGuardianModel model = new LegalGuardianModel(
                patients,
                dto.fullName(),
                dto.cpf(),
                dto.phoneNumber()
        );

        patients.forEach(p -> {
            if (!p.getLegalGuardians().contains(model)) {
                p.getLegalGuardians().add(model);
            }
        });

        return legalGuardianDTOMapper.apply(legalGuardianRepository.save(model));
    }

    @Transactional
    public LegalGuardianDTO updateGuardian(Long id, LegalGuardianUpdateDTO dto) {
        LegalGuardianModel existing = legalGuardianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Responsável legal não encontrado"));

        if (dto.fullName() != null) existing.setFullName(dto.fullName());
        if (dto.cpf() != null) existing.setCpf(dto.cpf());
        if (dto.phoneNumber() != null) existing.setPhoneNumber(dto.phoneNumber());

        if (dto.patientIds() != null) {
            List<PatientModel> patients = patientRepository.findAllById(dto.patientIds());

            if (patients.size() != dto.patientIds().size()) {
                throw new IllegalArgumentException("Um ou mais pacientes informados não existem");
            }

            // Remove vínculos antigos
            existing.getPatients().forEach(p -> p.getLegalGuardians().remove(existing));

            // Define novos vínculos
            existing.setPatients(patients);
            patients.forEach(p -> {
                if (!p.getLegalGuardians().contains(existing)) {
                    p.getLegalGuardians().add(existing);
                }
            });
        }

        return legalGuardianDTOMapper.apply(legalGuardianRepository.save(existing));
    }

    @Transactional
    public void deleteGuardian(Long id) {
        if (!legalGuardianRepository.existsById(id)) {
            throw new EntityNotFoundException("Responsável legal não encontrado");
        }
        legalGuardianRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LegalGuardianDTO getGuardianById(Long id) {
        return legalGuardianRepository.findById(id)
                .map(legalGuardianDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Responsável legal não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<LegalGuardianDTO> getAllGuardians() {
        return legalGuardianRepository.findAll()
                .stream()
                .map(legalGuardianDTOMapper)
                .collect(Collectors.toList());
    }
}
