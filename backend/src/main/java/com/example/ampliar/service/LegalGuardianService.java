package com.example.ampliar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ampliar.dto.legalGuardian.LegalGuardianCreateDTO;
import com.example.ampliar.dto.legalGuardian.LegalGuardianDTO;
import com.example.ampliar.dto.legalGuardian.LegalGuardianUpdateDTO;
import com.example.ampliar.mapper.LegalGuardianDTOMapper;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.LegalGuardianRepository;
import com.example.ampliar.repository.PatientRepository;
import com.example.ampliar.repository.PsychologistRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LegalGuardianService {

    private final LegalGuardianRepository legalGuardianRepository;
    private final PatientRepository patientRepository;
    private final LegalGuardianDTOMapper legalGuardianDTOMapper;
    private final PsychologistRepository psychologistRepository;

    public LegalGuardianService(
            LegalGuardianRepository legalGuardianRepository,
            PatientRepository patientRepository,
            LegalGuardianDTOMapper legalGuardianDTOMapper,
            PsychologistRepository psychologistRepository
    ) {
        this.legalGuardianRepository = legalGuardianRepository;
        this.patientRepository = patientRepository;
        this.legalGuardianDTOMapper = legalGuardianDTOMapper;
        this.psychologistRepository = psychologistRepository;
    }

    private PsychologistModel getAuthenticatedPsychologist() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return psychologistRepository.findByEmailAndDeletedAtIsNull(username)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo não encontrado com email: " + username));
    }

    @Transactional
    public LegalGuardianDTO createGuardian(LegalGuardianCreateDTO dto) {
        log.info("Criando responsável legal: {}", dto.fullName());

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            List<Long> requestedIds = dto.patientIds();
            List<PatientModel> patients = patientRepository.findByIdInAndPsychologistAndDeletedAtIsNull(requestedIds, psychologist);

            if (patients.size() != requestedIds.size()) {
                log.warn("Pacientes não encontrados ou excluídos para responsável legal. Esperados: {}, Encontrados: {}",
                         requestedIds.size(), patients.size());
                throw new IllegalArgumentException("Um ou mais pacientes informados não existem ou estão excluídos");
            }

            LegalGuardianModel model = new LegalGuardianModel(
                    patients,
                    dto.fullName(),
                    dto.cpf(),
                    dto.phoneNumber(),
                    psychologist
            );

            patients.forEach(p -> {
                if (!p.getLegalGuardians().contains(model)) {
                    p.getLegalGuardians().add(model);
                }
            });

            LegalGuardianDTO result = legalGuardianDTOMapper.apply(legalGuardianRepository.save(model));
            log.info("Responsável legal criado com sucesso ID: {} com {} pacientes",
                     result.id(), patients.size());
            return result;

        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar responsável legal: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar responsável legal", e);
            throw new RuntimeException("Erro interno ao criar responsável legal", e);
        }
    }

    @Transactional
    public LegalGuardianDTO updateGuardian(Long id, LegalGuardianUpdateDTO dto) {
        log.info("Atualizando responsável legal ID: {}", id);

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            LegalGuardianModel existing = legalGuardianRepository.findByIdAndPsychologistAndDeletedAtIsNull(id, psychologist)
                    .orElseThrow(() -> {
                        log.error("Responsável legal não encontrado para atualização ID: {}", id);
                        return new EntityNotFoundException("Responsável legal não encontrado");
                    });

            if (dto.fullName() != null) {
                existing.setFullName(dto.fullName());
                log.debug("Nome do responsável legal atualizado");
            }
            if (dto.cpf() != null) {
                existing.setCpf(dto.cpf());
                log.debug("CPF do responsável legal atualizado");
            }
            if (dto.phoneNumber() != null) {
                existing.setPhoneNumber(dto.phoneNumber());
                log.debug("Telefone do responsável legal atualizado");
            }

            if (dto.patientIds() != null) {
                List<PatientModel> patients = patientRepository.findByIdInAndPsychologistAndDeletedAtIsNull(dto.patientIds(), psychologist);

                if (patients.size() != dto.patientIds().size()) {
                    log.warn("Pacientes não encontrados ou excluídos na atualização. Esperados: {}, Encontrados: {}",
                             dto.patientIds().size(), patients.size());
                    throw new IllegalArgumentException("Um ou mais pacientes informados não existem ou estão excluídos");
                }

                existing.getPatients().forEach(p -> p.getLegalGuardians().remove(existing));

                existing.setPatients(patients);
                patients.forEach(p -> {
                    if (!p.getLegalGuardians().contains(existing)) {
                        p.getLegalGuardians().add(existing);
                    }
                });
                log.debug("Vínculos com pacientes atualizados. {} pacientes associados", patients.size());
            }

            LegalGuardianDTO result = legalGuardianDTOMapper.apply(legalGuardianRepository.save(existing));
            log.info("Responsável legal atualizado com sucesso ID: {}", id);
            return result;

        } catch (EntityNotFoundException e) {
            log.error("Responsável legal não encontrado para atualização ID: {}", id);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao atualizar responsável legal: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar responsável legal ID: {}", id, e);
            throw new RuntimeException("Erro interno ao atualizar responsável legal", e);
        }
    }

    @Transactional
    public void deleteGuardian(Long id) {
        log.info("Excluindo responsável legal ID: {}", id);

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            LegalGuardianModel guardian = legalGuardianRepository.findByIdAndPsychologistAndDeletedAtIsNull(id, psychologist)
                    .orElseThrow(() -> {
                        log.warn("Tentativa de excluir responsável legal inexistente ID: {}", id);
                        return new EntityNotFoundException("Responsável legal não encontrado");
                    });

            guardian.setDeletedAt(LocalDateTime.now());
            legalGuardianRepository.save(guardian);
            log.info("Responsável legal excluído com sucesso ID: {}", id);

        } catch (EntityNotFoundException e) {
            log.warn("Responsável legal não encontrado para exclusão ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao excluir responsável legal ID: {}", id, e);
            throw new RuntimeException("Erro interno ao excluir responsável legal", e);
        }
    }

    @Transactional(readOnly = true)
    public LegalGuardianDTO getGuardianById(Long id) {
        log.debug("Buscando responsável legal por ID: {}", id);

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            LegalGuardianDTO result = legalGuardianRepository.findByIdAndPsychologistAndDeletedAtIsNull(id, psychologist)
                    .map(legalGuardianDTOMapper)
                    .orElseThrow(() -> {
                        log.warn("Responsável legal não encontrado ID: {}", id);
                        return new EntityNotFoundException("Responsável legal não encontrado");
                    });
            log.debug("Responsável legal encontrado ID: {}", id);
            return result;

        } catch (EntityNotFoundException e) {
            log.warn("Responsável legal não encontrado na consulta ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar responsável legal ID: {}", id, e);
            throw new RuntimeException("Erro interno ao buscar responsável legal", e);
        }
    }

    @Transactional(readOnly = true)
    public List<LegalGuardianDTO> getAllGuardians() {
        log.debug("Buscando todos os responsáveis legais");

        PsychologistModel psychologist = getAuthenticatedPsychologist();

        try {
            List<LegalGuardianDTO> result = legalGuardianRepository.findAllByPsychologistAndDeletedAtIsNull(psychologist)
                    .stream()
                    .map(legalGuardianDTOMapper)
                    .collect(Collectors.toList());
            log.debug("Encontrados {} responsáveis legais", result.size());
            return result;
        } catch (Exception e) {
            log.error("Erro ao buscar todos os responsáveis legais", e);
            throw new RuntimeException("Erro interno ao buscar responsáveis legais", e);
        }
    }
}
