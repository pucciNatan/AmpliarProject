import type { Appointment, CalendarMonth, CalendarDay } from "@/models/appointment"

export class AppointmentController {
  private static instance: AppointmentController
  private appointments: Appointment[] = [
    {
      id: "1",
      patientId: "1",
      patientName: "Ana Carolina Santos",
      psychologistId: "1",
      psychologist: "Dra. Maria Silva",
      date: "2024-01-20",
      time: "09:00",
      endTime: "10:00",
      type: "Consulta Individual",
      status: "scheduled",
      notes: "Primeira consulta",
      paymentStatus: "paid",
      amount: 150,
    },
    {
      id: "2",
      patientId: "2",
      patientName: "João Pedro Silva",
      psychologistId: "1",
      psychologist: "Dra. Maria Silva",
      date: "2024-01-20",
      time: "10:30",
      endTime: "11:30",
      type: "Terapia Cognitiva",
      status: "scheduled",
      paymentStatus: "pending",
      amount: 150,
    },
    {
      id: "3",
      patientId: "3",
      patientName: "Maria Fernanda Costa",
      psychologistId: "1",
      psychologist: "Dra. Maria Silva",
      date: "2024-01-22",
      time: "14:00",
      endTime: "15:00",
      type: "Avaliação Inicial",
      status: "completed",
      paymentStatus: "paid",
      amount: 200,
    },
    {
      id: "4",
      patientId: "4",
      patientName: "Carlos Eduardo Lima",
      psychologistId: "1",
      psychologist: "Dra. Maria Silva",
      date: "2024-01-25",
      time: "15:30",
      endTime: "16:30",
      type: "Consulta Individual",
      status: "scheduled",
      paymentStatus: "overdue",
      amount: 150,
    },
  ]

  static getInstance(): AppointmentController {
    if (!AppointmentController.instance) {
      AppointmentController.instance = new AppointmentController()
    }
    return AppointmentController.instance
  }

  getAppointments(): Appointment[] {
    return [...this.appointments]
  }

  getAppointmentsByDate(date: string): Appointment[] {
    return this.appointments.filter((appointment) => appointment.date === date)
  }

  getCalendarMonth(year: number, month: number): CalendarMonth {
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)
    const startDate = new Date(firstDay)
    startDate.setDate(startDate.getDate() - firstDay.getDay())

    const days: CalendarDay[] = []
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    for (let i = 0; i < 42; i++) {
      const currentDate = new Date(startDate)
      currentDate.setDate(startDate.getDate() + i)

      const dateString = currentDate.toISOString().split("T")[0]
      const dayAppointments = this.getAppointmentsByDate(dateString)

      days.push({
        date: new Date(currentDate),
        appointments: dayAppointments,
        isCurrentMonth: currentDate.getMonth() === month,
        isToday: currentDate.getTime() === today.getTime(),
        isSelected: false,
      })
    }

    return {
      year,
      month,
      days,
    }
  }

  async createAppointment(appointment: Omit<Appointment, "id">): Promise<Appointment> {
    const newAppointment: Appointment = {
      ...appointment,
      id: Date.now().toString(),
    }

    this.appointments.push(newAppointment)
    return newAppointment
  }

  async updateAppointment(id: string, updates: Partial<Appointment>): Promise<Appointment | null> {
    const index = this.appointments.findIndex((apt) => apt.id === id)
    if (index === -1) return null

    this.appointments[index] = { ...this.appointments[index], ...updates }
    return this.appointments[index]
  }

  async deleteAppointment(id: string): Promise<boolean> {
    const index = this.appointments.findIndex((apt) => apt.id === id)
    if (index === -1) return false

    this.appointments.splice(index, 1)
    return true
  }
}
