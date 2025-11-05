package com.example.ampliar.dto.settings;

public record UserSettingsUpdateDTO(
        Boolean emailReminders,
        Boolean smsReminders,
        Boolean appointmentConfirmations,
        Boolean paymentReminders,
        String preferredTheme,
        String language,
        Boolean autoBackup,
        Integer sessionTimeoutMinutes,
        Integer defaultAppointmentDuration,
        Boolean twoFactorAuth,
        Integer passwordExpiryDays
) {}
