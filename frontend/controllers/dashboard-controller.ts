import { Appointment } from "@/models/appointment"
import { Patient } from "@/models/patient"
import { Payment } from "@/models/payment"
// Corrija as importações para usar as instâncias (lowercase)
import { appointmentController } from "./appointment-controller"
import { patientController } from "./patient-controller"
import { financeController } from "./finance-controller"

export interface DashboardStats {
  monthlyRevenue: number
  pendingRevenue: number
  activePatients: number
  todayAppointmentsCount: number
}

export interface DashboardData {
  stats: DashboardStats
  upcomingAppointments: Appointment[]
}

class DashboardController {
  async getDashboardData(): Promise<DashboardData> {
    // Busca todos os dados em paralelo
    const [appointments, payments, patients] = await Promise.all([
      appointmentController.getAppointments(),
      financeController.getPayments(),
      patientController.getPatients(),
    ])

    const now = new Date()
    const today = now.toISOString().split("T")[0]
    const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)

    // --- CORREÇÃO DA LÓGICA FINANCEIRA ---

    // 1. Receita Mensal: Soma todos os pagamentos (Payments) deste mês.
    const monthlyRevenue = payments
      .filter((p: Payment) => new Date(p.paymentDate) >= firstDayOfMonth)
      .reduce((sum: number, p: Payment) => sum + p.amount, 0)

    // 2. Pagamentos Pendentes: Soma os agendamentos (Appointments) com status "pending".
    const pendingRevenue = appointments
      .filter((a: Appointment) => a.paymentStatus === "pending")
      .reduce((sum: number, a: Appointment) => sum + a.paymentAmount, 0)

    // --- Fim da Correção ---

    const activePatients = patients.filter((p: Patient) => p.status === "active").length

    const todayAppointmentsCount = appointments.filter((a: Appointment) => a.date === today).length

    // Próximos agendamentos
    const upcomingAppointments = appointments
      .filter((a: Appointment) => new Date(a.date) >= now && a.status === "scheduled")
      .sort((a, b) => a.appointmentDate.localeCompare(b.appointmentDate))
      .slice(0, 5)

    return {
      stats: {
        monthlyRevenue,
        pendingRevenue,
        activePatients,
        todayAppointmentsCount,
      },
      upcomingAppointments,
    }
  }
}

export const dashboardController = new DashboardController()
