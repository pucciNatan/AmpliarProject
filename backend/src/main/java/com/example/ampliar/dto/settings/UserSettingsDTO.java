package com.example.ampliar.dto.settings;

public record UserSettingsDTO(
        boolean emailReminders,
        boolean smsReminders,
        boolean appointmentConfirmations,
        boolean paymentReminders,
        String preferredTheme,
        String language,
        boolean autoBackup,
        int sessionTimeoutMinutes,
        int defaultAppointmentDuration,
        boolean twoFactorAuth,
        int passwordExpiryDays
) {}
