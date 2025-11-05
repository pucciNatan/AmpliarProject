export interface PsychologistWorkingHour {
  dayOfWeek: string
  startTime: string | null
  endTime: string | null
  enabled: boolean
}

export interface Psychologist {
  id: string
  fullName: string
  email: string
  cpf: string
  phoneNumber: string
  crp?: string | null
  address?: string | null
  bio?: string | null
  specialties?: string[]
  workingHours?: PsychologistWorkingHour[]
}
