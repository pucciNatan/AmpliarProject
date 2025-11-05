package com.example.ampliar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSettingsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "psychologist_id", nullable = false, unique = true)
    private PsychologistModel psychologist;

    @Column(name = "email_reminders", nullable = false)
    private boolean emailReminders = true;

    @Column(name = "sms_reminders", nullable = false)
    private boolean smsReminders = false;

    @Column(name = "appointment_confirmations", nullable = false)
    private boolean appointmentConfirmations = true;

    @Column(name = "payment_reminders", nullable = false)
    private boolean paymentReminders = true;

    @Column(name = "preferred_theme", nullable = false, length = 20)
    private String preferredTheme = "system";

    @Column(name = "language", nullable = false, length = 10)
    private String language = "pt-BR";

    @Column(name = "auto_backup", nullable = false)
    private boolean autoBackup = true;

    @Column(name = "session_timeout_minutes", nullable = false)
    private int sessionTimeoutMinutes = 30;

    @Column(name = "default_appointment_duration", nullable = false)
    private int defaultAppointmentDuration = 60;

    @Column(name = "two_factor_auth", nullable = false)
    private boolean twoFactorAuth = false;

    @Column(name = "password_expiry_days", nullable = false)
    private int passwordExpiryDays = 90;

    public UserSettingsModel(PsychologistModel psychologist) {
        this.psychologist = psychologist;
    }

    public void setPsychologist(PsychologistModel psychologist) {
        this.psychologist = psychologist;
    }

    public void setEmailReminders(boolean emailReminders) {
        this.emailReminders = emailReminders;
    }

    public void setSmsReminders(boolean smsReminders) {
        this.smsReminders = smsReminders;
    }

    public void setAppointmentConfirmations(boolean appointmentConfirmations) {
        this.appointmentConfirmations = appointmentConfirmations;
    }

    public void setPaymentReminders(boolean paymentReminders) {
        this.paymentReminders = paymentReminders;
    }

    public void setPreferredTheme(String preferredTheme) {
        if (preferredTheme == null || preferredTheme.trim().isEmpty()) {
            this.preferredTheme = "system";
        } else {
            this.preferredTheme = preferredTheme.trim().toLowerCase();
        }
    }

    public void setLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            this.language = "pt-BR";
        } else {
            this.language = language.trim();
        }
    }

    public void setAutoBackup(boolean autoBackup) {
        this.autoBackup = autoBackup;
    }

    public void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = Math.max(5, sessionTimeoutMinutes);
    }

    public void setDefaultAppointmentDuration(int defaultAppointmentDuration) {
        this.defaultAppointmentDuration = Math.max(15, defaultAppointmentDuration);
    }

    public void setTwoFactorAuth(boolean twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
    }

    public void setPasswordExpiryDays(int passwordExpiryDays) {
        this.passwordExpiryDays = Math.max(0, passwordExpiryDays);
    }
}
