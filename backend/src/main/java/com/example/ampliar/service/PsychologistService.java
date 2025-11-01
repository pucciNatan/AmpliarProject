package com.example.ampliar.service;

import com.example.ampliar.dto.psychologist.PsychologistCreateDTO;
import com.example.ampliar.dto.psychologist.PsychologistDTO;
import com.example.ampliar.dto.psychologist.PsychologistUpdateDTO;
import com.example.ampliar.mapper.PsychologistDTOMapper;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.PsychologistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PsychologistService {

    private final PsychologistRepository psychologistRepository;
    private final PasswordEncoder passwordEncoder;
    private final PsychologistDTOMapper psychologistDTOMapper;

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
}