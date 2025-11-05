export type AppointmentStatus = "scheduled" | "completed" | "cancelled" | "no-show"

export type BackendAppointmentStatus = "SCHEDULED" | "COMPLETED" | "CANCELLED" | "NO_SHOW"

export interface AppointmentPatient {
  id: string
  fullName: string
}

export interface AppointmentPsychologist {
  id: string
  fullName: string
}

export interface Appointment {
  id: string
  appointmentDate: string
  appointmentEndDate: string | null
  date: string
  time: string | null
  endTime: string | null
  type: string
  status: AppointmentStatus
  backendStatus: BackendAppointmentStatus
  notes?: string | null
  patients: AppointmentPatient[]
  primaryPatientName: string | null
  psychologist: AppointmentPsychologist | null
  paymentStatus: "paid" | "pending" | "overdue"
  paymentAmount: number
  paymentId?: string | null
}

export interface CreateAppointmentPayload {
  appointmentDate: string
  appointmentEndDate?: string | null
  status?: BackendAppointmentStatus
  type: string
  notes?: string | null
  psychologistId: number
  patientIds: number[]
  paymentId?: number | null
}

export interface UpdateAppointmentPayload {
  appointmentDate?: string
  appointmentEndDate?: string | null
  status?: BackendAppointmentStatus
  type?: string
  notes?: string | null
  psychologistId?: number
  patientIds?: number[]
  paymentId?: number | null
}

export interface CalendarDay {
  date: Date
  appointments: Appointment[]
  isCurrentMonth: boolean
  isToday: boolean
  isSelected: boolean
}

export interface CalendarMonth {
  year: number
  month: number
  days: CalendarDay[]
}
