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
        PsychologistModel psychologist = psychologistRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Psicólogo não encontrado com email: " + email));

        return User.builder()
                .username(psychologist.getEmail())
                .password(psychologist.getPassword())
                .build();
    }
}
