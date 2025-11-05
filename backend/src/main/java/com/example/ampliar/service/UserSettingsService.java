package com.example.ampliar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ampliar.dto.settings.UserSettingsDTO;
import com.example.ampliar.dto.settings.UserSettingsUpdateDTO;
import com.example.ampliar.mapper.UserSettingsMapper;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.model.UserSettingsModel;
import com.example.ampliar.repository.PsychologistRepository;
import com.example.ampliar.repository.UserSettingsRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final PsychologistRepository psychologistRepository;
    private final UserSettingsMapper mapper;

    public UserSettingsService(
            UserSettingsRepository userSettingsRepository,
            PsychologistRepository psychologistRepository,
            UserSettingsMapper mapper
    ) {
        this.userSettingsRepository = userSettingsRepository;
        this.psychologistRepository = psychologistRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public UserSettingsDTO getSettings(Long psychologistId) {
        log.debug("Buscando configurações para psicólogo ID: {}", psychologistId);
        UserSettingsModel settings = userSettingsRepository.findByPsychologistId(psychologistId)
                .orElseGet(() -> createDefaultSettings(psychologistId));
        return mapper.apply(settings);
    }

    @Transactional
    public UserSettingsDTO updateSettings(Long psychologistId, UserSettingsUpdateDTO dto) {
        log.info("Atualizando configurações para psicólogo ID: {}", psychologistId);

        UserSettingsModel settings = userSettingsRepository.findByPsychologistId(psychologistId)
                .orElseGet(() -> createDefaultSettings(psychologistId));

        if (dto.emailReminders() != null) {
            settings.setEmailReminders(dto.emailReminders());
        }
        if (dto.smsReminders() != null) {
            settings.setSmsReminders(dto.smsReminders());
        }
        if (dto.appointmentConfirmations() != null) {
            settings.setAppointmentConfirmations(dto.appointmentConfirmations());
        }
        if (dto.paymentReminders() != null) {
            settings.setPaymentReminders(dto.paymentReminders());
        }
        if (dto.preferredTheme() != null) {
            settings.setPreferredTheme(dto.preferredTheme());
        }
        if (dto.language() != null) {
            settings.setLanguage(dto.language());
        }
        if (dto.autoBackup() != null) {
            settings.setAutoBackup(dto.autoBackup());
        }
        if (dto.sessionTimeoutMinutes() != null) {
            settings.setSessionTimeoutMinutes(dto.sessionTimeoutMinutes());
        }
        if (dto.defaultAppointmentDuration() != null) {
            settings.setDefaultAppointmentDuration(dto.defaultAppointmentDuration());
        }
        if (dto.twoFactorAuth() != null) {
            settings.setTwoFactorAuth(dto.twoFactorAuth());
        }
        if (dto.passwordExpiryDays() != null) {
            settings.setPasswordExpiryDays(dto.passwordExpiryDays());
        }

        UserSettingsModel saved = userSettingsRepository.save(settings);
        log.info("Configurações atualizadas para psicólogo ID: {}", psychologistId);
        return mapper.apply(saved);
    }

    private UserSettingsModel createDefaultSettings(Long psychologistId) {
        log.debug("Criando configurações padrão para psicólogo ID: {}", psychologistId);
        PsychologistModel psychologist = psychologistRepository.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo não encontrado"));

        UserSettingsModel settings = new UserSettingsModel(psychologist);
        UserSettingsModel saved = userSettingsRepository.save(settings);
        return saved;
    }
}
