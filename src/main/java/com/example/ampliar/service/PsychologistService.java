package com.example.ampliar.service;

import com.example.ampliar.dto.PsychologistCreateDTO;
import com.example.ampliar.dto.PsychologistDTO;
import com.example.ampliar.mapper.PsychologistDTOMapper;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.PsychologistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public PsychologistDTO createPsychologist(PsychologistCreateDTO dto) {
        try {
            PsychologistModel model = new PsychologistModel(
                    dto.fullName(),
                    dto.cpf(),
                    dto.phoneNumber(),
                    dto.email(),
                    passwordEncoder.encode(dto.password())
            );
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            return psychologistDTOMapper.apply(psychologistRepository.save(model));
        }

    }

    public PsychologistDTO updatePsychologist(Long id, PsychologistCreateDTO dto) {
        PsychologistModel existing = psychologistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo não encontrado"));

        existing.setFullName(dto.fullName());
        existing.setCpf(dto.cpf());
        existing.setPhoneNumber(dto.phoneNumber());
        existing.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.password()));
        }

        return psychologistDTOMapper.apply(psychologistRepository.save(existing));
    }

    public PsychologistDTO getPsychologistById(Long id) {
        return psychologistRepository.findById(id)
                .map(psychologistDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo não encontrado"));
    }

    public Optional<PsychologistModel> findByEmail(String email) {
        return psychologistRepository.findByEmail(email);
    }

    public List<PsychologistDTO> getAllPsychologists() {
        return psychologistRepository.findAll()
                .stream()
                .map(psychologistDTOMapper)
                .collect(Collectors.toList());
    }

    public void deletePsychologist(Long id) {
        if (!psychologistRepository.existsById(id)) {
            throw new EntityNotFoundException("Psicólogo não encontrado");
        }
        psychologistRepository.deleteById(id);
    }
}
