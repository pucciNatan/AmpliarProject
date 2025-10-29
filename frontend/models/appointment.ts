export interface Appointment {
  id: string
  patientId: string
  patientName: string
  psychologistId: string
  psychologist: string
  date: string
  time: string
  endTime: string
  type: string
  status: "scheduled" | "completed" | "cancelled" | "no-show"
  notes?: string
  paymentStatus: "paid" | "pending" | "overdue"
  amount: number
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
