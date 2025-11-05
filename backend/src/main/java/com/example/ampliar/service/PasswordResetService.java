package com.example.ampliar.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ampliar.model.PasswordResetTokenModel;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.PasswordResetTokenRepository;
import com.example.ampliar.repository.PsychologistRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PasswordResetService {

    private final PsychologistRepository psychologistRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PsychologistRepository psychologistRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.psychologistRepository = psychologistRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String createPasswordResetToken(String email) {
        log.info("Gerando token de redefinição de senha para: {}", email);
        PsychologistModel psychologist = psychologistRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // opcional: limpar tokens expirados
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        PasswordResetTokenModel resetToken = new PasswordResetTokenModel(token, psychologist, expiresAt);
        tokenRepository.save(resetToken);

        log.info("Token de redefinição gerado para usuário {} expira em {}", psychologist.getEmail(), expiresAt);
        return token;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.info("Processando redefinição de senha por token");
        PasswordResetTokenModel tokenModel = tokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new EntityNotFoundException("Token inválido"));

        if (tokenModel.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Token expirado para usuário {}", tokenModel.getPsychologist().getEmail());
            throw new IllegalStateException("Token expirado");
        }

        PsychologistModel psychologist = tokenModel.getPsychologist();
        psychologist.setPassword(passwordEncoder.encode(newPassword));
        psychologistRepository.save(psychologist);
        log.info("Senha redefinida para usuário {}", psychologist.getEmail());

        tokenModel.setUsed(true);
        tokenRepository.save(tokenModel);
    }
}
