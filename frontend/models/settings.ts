export interface UserSettings {
  emailReminders: boolean
  smsReminders: boolean
  appointmentConfirmations: boolean
  paymentReminders: boolean
  preferredTheme: "light" | "dark" | "system"
  language: string
  autoBackup: boolean
  sessionTimeoutMinutes: number
  defaultAppointmentDuration: number
  twoFactorAuth: boolean
  passwordExpiryDays: number
}

export type UserSettingsUpdate = Partial<UserSettings>
