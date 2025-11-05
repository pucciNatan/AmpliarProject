import { api } from "@/lib/api-client"
import type { UserSettings, UserSettingsUpdate } from "@/models/settings"

interface UserSettingsDTO {
  emailReminders: boolean
  smsReminders: boolean
  appointmentConfirmations: boolean
  paymentReminders: boolean
  preferredTheme: string
  language: string
  autoBackup: boolean
  sessionTimeoutMinutes: number
  defaultAppointmentDuration: number
  twoFactorAuth: boolean
  passwordExpiryDays: number
}

const mapDtoToSettings = (dto: UserSettingsDTO): UserSettings => ({
  emailReminders: dto.emailReminders,
  smsReminders: dto.smsReminders,
  appointmentConfirmations: dto.appointmentConfirmations,
  paymentReminders: dto.paymentReminders,
  preferredTheme: (dto.preferredTheme as UserSettings["preferredTheme"]) ?? "system",
  language: dto.language,
  autoBackup: dto.autoBackup,
  sessionTimeoutMinutes: dto.sessionTimeoutMinutes,
  defaultAppointmentDuration: dto.defaultAppointmentDuration,
  twoFactorAuth: dto.twoFactorAuth,
  passwordExpiryDays: dto.passwordExpiryDays,
})

export class SettingsController {
  private static instance: SettingsController

  static getInstance(): SettingsController {
    if (!SettingsController.instance) {
      SettingsController.instance = new SettingsController()
    }

    return SettingsController.instance
  }

  async getSettings(psychologistId: string): Promise<UserSettings> {
    const response = (await api(`/settings/${psychologistId}`, { method: "GET" })) as UserSettingsDTO
    return mapDtoToSettings(response)
  }

  async updateSettings(psychologistId: string, payload: UserSettingsUpdate): Promise<UserSettings> {
    const body: Record<string, unknown> = { ...payload }
    const response = (await api(`/settings/${psychologistId}`, {
      method: "PUT",
      body,
    })) as UserSettingsDTO
    return mapDtoToSettings(response)
  }
}
