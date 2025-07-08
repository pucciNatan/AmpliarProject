package com.example.ampliar.service;

import com.example.ampliar.models.PsychologistModel;
import com.example.ampliar.repository.PsychologistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PsychologistService {

    @Autowired
    private PsychologistRepository psychologistRepository;

    public PsychologistModel createPsychologist(PsychologistModel psychologist) {
        return psychologistRepository.save(psychologist);
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

    public List<PsychologistModel> getAllPsychologists() {
        return psychologistRepository.findAll();
    }
}
