package com.example.ampliar.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.PsychologistRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PsychologistDetailsService implements UserDetailsService {

    private final PsychologistRepository psychologistRepository;

    public PsychologistDetailsService(PsychologistRepository psychologistRepository) {
        this.psychologistRepository = psychologistRepository;
    }

@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Iniciando autenticação para o email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            log.warn("Tentativa de autenticação com email vazio ou nulo");
            throw new UsernameNotFoundException("O email informado é inválido ou vazio");
        }

        String normalizedEmail = email.trim().toLowerCase();
        log.debug("Email normalizado para busca: {}", normalizedEmail);

        try {
            PsychologistModel psychologist = psychologistRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> {
                        log.warn("Psicólogo não encontrado com o email: {}", normalizedEmail);
                        return new UsernameNotFoundException("Psicólogo não encontrado com o email: " + normalizedEmail);
                    });

            log.info("Usuário autenticado com sucesso: {}", normalizedEmail);

            return User.builder()
                    .username(psychologist.getEmail())
                    .password(psychologist.getPassword())
                    .roles("USER") // <-- ADICIONE ESTA LINHA
                    .build();

        } catch (UsernameNotFoundException e) {
            log.error("Falha na autenticação - usuário não encontrado: {}", normalizedEmail);
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado durante a autenticação para: {}", normalizedEmail, e);
            throw new UsernameNotFoundException("Erro interno durante a autenticação", e);
        }
    }
}
