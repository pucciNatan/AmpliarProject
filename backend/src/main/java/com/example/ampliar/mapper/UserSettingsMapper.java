package com.example.ampliar.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.example.ampliar.dto.settings.UserSettingsDTO;
import com.example.ampliar.model.UserSettingsModel;

@Component
public class UserSettingsMapper implements Function<UserSettingsModel, UserSettingsDTO> {

    @Override
    public UserSettingsDTO apply(UserSettingsModel source) {
        return new UserSettingsDTO(
                source.isEmailReminders(),
                source.isSmsReminders(),
                source.isAppointmentConfirmations(),
                source.isPaymentReminders(),
                source.getPreferredTheme(),
                source.getLanguage(),
                source.isAutoBackup(),
                source.getSessionTimeoutMinutes(),
                source.getDefaultAppointmentDuration(),
                source.isTwoFactorAuth(),
                source.getPasswordExpiryDays()
        );
    }
}
