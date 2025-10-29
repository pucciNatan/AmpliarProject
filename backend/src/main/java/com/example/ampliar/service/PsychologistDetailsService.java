package com.example.ampliar.service;

import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.PsychologistRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PsychologistDetailsService implements UserDetailsService {

    private final PsychologistRepository psychologistRepository;

    public PsychologistDetailsService(PsychologistRepository psychologistRepository) {
        this.psychologistRepository = psychologistRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("O email informado é inválido ou vazio");
        }

        PsychologistModel psychologist = psychologistRepository.findByEmail(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Psicólogo não encontrado com o email: " + email));

        return User.builder()
                .username(psychologist.getEmail())
                .password(psychologist.getPassword())
                .build();
    }
}
