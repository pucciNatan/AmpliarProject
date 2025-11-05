package com.example.ampliar.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ampliar.dto.psychologist.PsychologistCreateDTO;
import com.example.ampliar.dto.psychologist.PsychologistDTO;
import com.example.ampliar.dto.psychologist.PsychologistUpdateDTO;
import com.example.ampliar.dto.psychologist.PsychologistWorkingHourDTO;
import com.example.ampliar.mapper.PsychologistDTOMapper;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.model.PsychologistWorkingHour;
import com.example.ampliar.repository.PsychologistRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PsychologistService {

    private final PsychologistRepository psychologistRepository;
    private final PasswordEncoder passwordEncoder;
    private final PsychologistDTOMapper psychologistDTOMapper;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public PsychologistService(
            PsychologistRepository psychologistRepository,
            PasswordEncoder passwordEncoder,
            PsychologistDTOMapper psychologistDTOMapper
    ) {
        this.psychologistRepository = psychologistRepository;
        this.passwordEncoder = passwordEncoder;
        this.psychologistDTOMapper = psychologistDTOMapper;
    }

    @Transactional
    public PsychologistDTO createPsychologist(PsychologistCreateDTO dto) {
        log.info("Iniciando criação de psicólogo: {}", dto.email());

        try {
            if (psychologistRepository.findByEmail(dto.email()).isPresent()) {
                log.warn("Tentativa de criar psicólogo com email duplicado: {}", dto.email());
                throw new IllegalArgumentException("Já existe um psicólogo com esse e-mail.");
            }

            if (psychologistRepository.findByCpf(dto.cpf()).isPresent()) {
                log.warn("Tentativa de criar psicólogo com CPF duplicado: {}", dto.cpf());
                throw new IllegalArgumentException("Já existe um psicólogo com esse CPF.");
            }

            PsychologistModel model = new PsychologistModel(
                    dto.fullName(),
                    dto.cpf(),
                    dto.phoneNumber(),
                    dto.email(),
                    passwordEncoder.encode(dto.password())
            );

            applyProfileData(
                    model,
                    dto.crp(),
                    dto.address(),
                    dto.bio(),
                    dto.specialties(),
                    dto.workingHours()
            );

            PsychologistDTO result = psychologistDTOMapper.apply(psychologistRepository.save(model));
            log.info("Psicólogo criado com sucesso ID: {}", result.id());
            return result;

        } catch (IllegalArgumentException e) {
            log.error("Erro de validação ao criar psicólogo: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar psicólogo", e);
            throw new RuntimeException("Erro interno ao criar psicólogo", e);
        }
    }

    @Transactional
    public PsychologistDTO updatePsychologist(Long id, PsychologistUpdateDTO dto) {
        log.info("Iniciando atualização do psicólogo ID: {}", id);

        try {
            PsychologistModel existing = psychologistRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Psicólogo não encontrado para atualização ID: {}", id);
                        return new EntityNotFoundException("Psicólogo não encontrado");
                    });

            if (dto.fullName() != null) {
                existing.setFullName(dto.fullName());
                log.debug("Nome atualizado para: {}", dto.fullName());
            }
            if (dto.cpf() != null) {
                existing.setCpf(dto.cpf());
                log.debug("CPF atualizado");
            }
            if (dto.phoneNumber() != null) {
                existing.setPhoneNumber(dto.phoneNumber());
                log.debug("Telefone atualizado");
            }
            if (dto.email() != null) {
                existing.setEmail(dto.email());
                log.debug("Email atualizado para: {}", dto.email());
            }

            if (dto.password() != null && !dto.password().isBlank()) {
                existing.setPassword(passwordEncoder.encode(dto.password()));
                log.debug("Senha atualizada");
            }

            applyProfileData(
                    existing,
                    dto.crp(),
                    dto.address(),
                    dto.bio(),
                    dto.specialties(),
                    dto.workingHours()
            );

            PsychologistDTO result = psychologistDTOMapper.apply(psychologistRepository.save(existing));
            log.info("Psicólogo atualizado com sucesso ID: {}", id);
            return result;

        } catch (EntityNotFoundException e) {
            log.error("Psicólogo não encontrado para atualização ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar psicólogo ID: {}", id, e);
            throw new RuntimeException("Erro interno ao atualizar psicólogo", e);
        }
    }

    @Transactional
    public void deletePsychologist(Long id) {
        log.info("Iniciando exclusão do psicólogo ID: {}", id);

        try {
            if (!psychologistRepository.existsById(id)) {
                log.warn("Tentativa de excluir psicólogo inexistente ID: {}", id);
                throw new EntityNotFoundException("Psicólogo não encontrado");
            }

            psychologistRepository.deleteById(id);
            log.info("Psicólogo excluído com sucesso ID: {}", id);

        } catch (EntityNotFoundException e) {
            log.warn("Psicólogo não encontrado para exclusão ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao excluir psicólogo ID: {}", id, e);
            throw new RuntimeException("Erro interno ao excluir psicólogo", e);
        }
    }

    @Transactional(readOnly = true)
    public PsychologistDTO getPsychologistById(Long id) {
        log.debug("Buscando psicólogo por ID: {}", id);

        try {
            PsychologistDTO result = psychologistRepository.findById(id)
                    .map(psychologistDTOMapper)
                    .orElseThrow(() -> {
                        log.warn("Psicólogo não encontrado ID: {}", id);
                        return new EntityNotFoundException("Psicólogo não encontrado");
                    });
            log.debug("Psicólogo encontrado ID: {}", id);
            return result;

        } catch (EntityNotFoundException e) {
            log.warn("Psicólogo não encontrado na consulta ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar psicólogo ID: {}", id, e);
            throw new RuntimeException("Erro interno ao buscar psicólogo", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<PsychologistModel> findByEmail(String email) {
        log.debug("Buscando psicólogo por email: {}", email);

        try {
            Optional<PsychologistModel> result = psychologistRepository.findByEmail(email);
            if (result.isPresent()) {
                log.debug("Psicólogo encontrado por email: {}", email);
            } else {
                log.debug("Psicólogo não encontrado por email: {}", email);
            }
            return result;
        } catch (Exception e) {
            log.error("Erro ao buscar psicólogo por email: {}", email, e);
            throw new RuntimeException("Erro interno ao buscar psicólogo por email", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PsychologistDTO> getAllPsychologists() {
        log.debug("Buscando todos os psicólogos");

        try {
            List<PsychologistDTO> result = psychologistRepository.findAll()
                    .stream()
                    .map(psychologistDTOMapper)
                    .toList();
            log.debug("Encontrados {} psicólogos", result.size());
            return result;
        } catch (Exception e) {
            log.error("Erro ao buscar todos os psicólogos", e);
            throw new RuntimeException("Erro interno ao buscar psicólogos", e);
        }
    }

    private void applyProfileData(
            PsychologistModel model,
            String crp,
            String address,
            String bio,
            List<String> specialties,
            List<PsychologistWorkingHourDTO> workingHours
    ) {
        if (crp != null) {
            model.setCrp(crp);
            log.debug("CRP atualizado");
        }

        if (address != null) {
            model.setAddress(address);
            log.debug("Endereço atualizado");
        }

        if (bio != null) {
            model.setBio(bio);
            log.debug("Biografia atualizada");
        }

        if (specialties != null) {
            List<String> cleaned = specialties.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .collect(Collectors.toList());
            model.setSpecialties(cleaned);
            log.debug("Especialidades atualizadas - total: {}", cleaned.size());
        }

        if (workingHours != null) {
            List<PsychologistWorkingHour> slots = workingHours.stream()
                    .filter(Objects::nonNull)
                    .map(this::mapWorkingHour)
                    .collect(Collectors.toList());
            model.setWorkingHours(slots);
            log.debug("Horários de atendimento atualizados - total: {}", slots.size());
        }
    }

    private PsychologistWorkingHour mapWorkingHour(PsychologistWorkingHourDTO dto) {
        String day = dto.dayOfWeek();
        if (day == null || day.trim().isEmpty()) {
            throw new IllegalArgumentException("O dia da semana é obrigatório nos horários de atendimento");
        }

        boolean enabled = dto.enabled() != null && dto.enabled();
        LocalTime start = parseTime(dto.startTime());
        LocalTime end = parseTime(dto.endTime());

        if (!enabled) {
            start = null;
            end = null;
        }

        return new PsychologistWorkingHour(day, start, end, enabled);
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return LocalTime.parse(trimmed.length() == 5 ? trimmed : trimmed.substring(0, Math.min(5, trimmed.length())), TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Horário inválido: " + value, ex);
        }
    }
}
