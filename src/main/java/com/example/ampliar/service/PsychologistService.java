package com.example.ampliar.service;

import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.PsychologistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PsychologistService {

    @Autowired
    private final PsychologistRepository psychologistRepository;
    private final PasswordEncoder passwordEncoder;

    public PsychologistService(PsychologistRepository psychologistRepository){
        this.psychologistRepository = psychologistRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public PsychologistModel createPsychologist(PsychologistModel psychologist) {
        String fullName = psychologist.getFullName();
        String password = psychologist.getPassword();
        String cpf = psychologist.getCpf();
        String phoneNumber = psychologist.getPhoneNumber();
        String email = psychologist.getEmail();
        String criptoPassword = passwordEncoder.encode(password);
        PsychologistModel psychologistModel = new PsychologistModel(fullName, cpf, phoneNumber, email, criptoPassword);
        return psychologistRepository.save(psychologistModel);
    }

    public PsychologistModel updatePsychologist(Long id, PsychologistModel updatedPsychologist) {
        PsychologistModel existing = psychologistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psychologist not found"));

        existing.setFullName(updatedPsychologist.getFullName());
        existing.setCpf(updatedPsychologist.getCpf());
        existing.setPhoneNumber(updatedPsychologist.getPhoneNumber());
        existing.setEmail(updatedPsychologist.getEmail());
        existing.setPassword(updatedPsychologist.getPassword());

        return psychologistRepository.save(existing);
    }

    public void deletePsychologist(Long id) {
        if (!psychologistRepository.existsById(id)) {
            throw new EntityNotFoundException("Psychologist not found");
        }
        psychologistRepository.deleteById(id);
    }

    public PsychologistModel getPsychologistById(Long id) {
        return psychologistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psychologist not found"));
    }

    public Optional<PsychologistModel> findByEmail(String email) {
        return psychologistRepository.findByEmail(email);
    }

    public List<PsychologistModel> getAllPsychologists() {
        return psychologistRepository.findAll();
    }
}
