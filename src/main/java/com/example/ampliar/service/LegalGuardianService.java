package com.example.ampliar.service;

import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.repository.LegalGuardianRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegalGuardianService {

    @Autowired
    private LegalGuardianRepository legalGuardianRepository;

    public LegalGuardianModel createGuardian(LegalGuardianModel guardian) {
        return legalGuardianRepository.save(guardian);
    }

    public LegalGuardianModel updateGuardian(Long id, LegalGuardianModel updatedGuardian) {
        LegalGuardianModel existingGuardian = legalGuardianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Legal guardian not found"));

        existingGuardian.setFullName(updatedGuardian.getFullName());
        existingGuardian.setCpf(updatedGuardian.getCpf());
        existingGuardian.setPhoneNumber(updatedGuardian.getPhoneNumber());
        existingGuardian.setPatients(updatedGuardian.getPatients());

        return legalGuardianRepository.save(existingGuardian);
    }

    public void deleteGuardian(Long id) {
        if (!legalGuardianRepository.existsById(id)) {
            throw new EntityNotFoundException("Legal guardian not found");
        }
        legalGuardianRepository.deleteById(id);
    }

    public LegalGuardianModel getGuardianById(Long id) {
        return legalGuardianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Legal guardian not found"));
    }

    public List<LegalGuardianModel> getAllGuardians() {
        return legalGuardianRepository.findAll();
    }
}
