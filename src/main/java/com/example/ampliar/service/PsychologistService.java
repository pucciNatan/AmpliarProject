package com.example.ampliar.service;

import com.example.ampliar.dto.PsychologistCreateDTO;
import com.example.ampliar.dto.PsychologistDTO;
import com.example.ampliar.dto.PsychologistUpdateDTO;
import com.example.ampliar.mapper.PsychologistDTOMapper;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.PsychologistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
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
        if (psychologistRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Já existe um psicólogo com esse e-mail.");
        }

        if (psychologistRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um psicólogo com esse CPF.");
        }

        PsychologistModel model = new PsychologistModel(
                dto.fullName(),
                dto.cpf(),
                dto.phoneNumber(),
                dto.email(),
                passwordEncoder.encode(dto.password())
        );

        return psychologistDTOMapper.apply(psychologistRepository.save(model));
    }

    @Transactional
    public PsychologistDTO updatePsychologist(Long id, PsychologistUpdateDTO dto) {
        PsychologistModel existing = psychologistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo não encontrado"));

        if (dto.fullName() != null) existing.setFullName(dto.fullName());
        if (dto.cpf() != null) existing.setCpf(dto.cpf());
        if (dto.phoneNumber() != null) existing.setPhoneNumber(dto.phoneNumber());
        if (dto.email() != null) existing.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.password()));
        }

        return psychologistDTOMapper.apply(psychologistRepository.save(existing));
    }

    @Transactional
    public void deletePsychologist(Long id) {
        if (!psychologistRepository.existsById(id)) {
            throw new EntityNotFoundException("Psicólogo não encontrado");
        }
        psychologistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PsychologistDTO getPsychologistById(Long id) {
        return psychologistRepository.findById(id)
                .map(psychologistDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo não encontrado"));
    }

    @Transactional(readOnly = true)
    public Optional<PsychologistModel> findByEmail(String email) {
        return psychologistRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<PsychologistDTO> getAllPsychologists() {
        return psychologistRepository.findAll()
                .stream()
                .map(psychologistDTOMapper)
                .toList();
    }
}
