import { api } from "@/lib/api-client"
import type {
  Appointment,
  AppointmentStatus,
  BackendAppointmentStatus,
  CreateAppointmentPayload,
  UpdateAppointmentPayload,
} from "@/models/appointment"

interface AppointmentPatientDTO {
  id: number
  fullName: string
}

interface AppointmentPsychologistDTO {
  id: number
  fullName: string
}

interface AppointmentDTO {
  id: number
  appointmentDate: string
  appointmentEndDate: string | null
  status: BackendAppointmentStatus
  type: string
  notes?: string | null
  psychologist?: AppointmentPsychologistDTO | null
  patients: AppointmentPatientDTO[]
  paymentStatus: "paid" | "pending" | "overdue"
  paymentAmount: number | string | null
  paymentId?: number | null
}

const backendToUiStatusMap: Record<BackendAppointmentStatus, AppointmentStatus> = {
  SCHEDULED: "scheduled",
  COMPLETED: "completed",
  CANCELLED: "cancelled",
  NO_SHOW: "no-show",
}

const uiToBackendStatusMap: Record<AppointmentStatus, BackendAppointmentStatus> = {
  scheduled: "SCHEDULED",
  completed: "COMPLETED",
  cancelled: "CANCELLED",
  "no-show": "NO_SHOW",
}

const ensureBackendStatus = (status?: BackendAppointmentStatus): BackendAppointmentStatus => {
  if (!status) return "SCHEDULED"
  return status
}

const toBackendStatus = (status?: AppointmentStatus): BackendAppointmentStatus | undefined => {
  if (!status) return undefined
  return uiToBackendStatusMap[status]
}

const extractDate = (value?: string | null): string => {
  if (!value) return ""
  const [datePart] = value.split("T")
  return datePart ?? ""
}

const extractTime = (value?: string | null): string | null => {
  if (!value) return null
  const [, timePart] = value.split("T")
  if (!timePart) return null
  return timePart.substring(0, 5)
}

const normalizeAmount = (value: number | string | null): number => {
  if (value === null || value === undefined) return 0
  if (typeof value === "number") return value
  const parsed = Number(value)
  return Number.isNaN(parsed) ? 0 : parsed
}

const mapDtoToAppointment = (dto: AppointmentDTO): Appointment => {
  const backendStatus = ensureBackendStatus(dto.status)

  const patients = dto.patients?.map((patient) => ({
    id: patient.id.toString(),
    fullName: patient.fullName,
  })) ?? []

  const psychologist = dto.psychologist
    ? {
        id: dto.psychologist.id.toString(),
        fullName: dto.psychologist.fullName,
      }
    : null

  return {
    id: dto.id.toString(),
    appointmentDate: dto.appointmentDate,
    appointmentEndDate: dto.appointmentEndDate ?? null,
    date: extractDate(dto.appointmentDate),
    time: extractTime(dto.appointmentDate),
    endTime: extractTime(dto.appointmentEndDate),
    type: dto.type,
    status: backendToUiStatusMap[backendStatus],
    backendStatus,
    notes: dto.notes ?? null,
    patients,
    primaryPatientName: patients[0]?.fullName ?? null,
    psychologist,
    paymentStatus: (dto.paymentStatus ?? "pending") as Appointment["paymentStatus"],
    paymentAmount: normalizeAmount(dto.paymentAmount),
    paymentId: dto.paymentId != null ? dto.paymentId.toString() : null,
  }
}

export class AppointmentController {
  private static instance: AppointmentController
  private cache: Appointment[] = []
  private loaded = false
  private loadingPromise: Promise<Appointment[]> | null = null

  static getInstance(): AppointmentController {
    if (!AppointmentController.instance) {
      AppointmentController.instance = new AppointmentController()
    }

    return AppointmentController.instance
  }

  private sortAppointments(appointments: Appointment[]): Appointment[] {
    return appointments.sort((a, b) => a.appointmentDate.localeCompare(b.appointmentDate))
  }

  private async loadAppointments(force = false): Promise<Appointment[]> {
    if (this.loaded && !force) {
      return this.cache
    }

    if (this.loadingPromise && !force) {
      return this.loadingPromise
    }

    this.loadingPromise = (async () => {
      const response = (await api("/appointments", { method: "GET" })) as AppointmentDTO[]
      this.cache = this.sortAppointments(response.map(mapDtoToAppointment))
      this.loaded = true
      return this.cache
    })()

    try {
      return await this.loadingPromise
    } finally {
      this.loadingPromise = null
    }
  }

  async getAppointments(options: { force?: boolean } = {}): Promise<Appointment[]> {
    return this.loadAppointments(!!options.force)
  }

  async getAppointmentById(id: string): Promise<Appointment> {
    const response = (await api(`/appointments/${id}`, { method: "GET" })) as AppointmentDTO
    return mapDtoToAppointment(response)
  }

  async getAppointmentsByDate(date: string): Promise<Appointment[]> {
    const appointments = await this.getAppointments()
    return appointments.filter((appointment) => appointment.date === date)
  }

  async createAppointment(payload: CreateAppointmentPayload): Promise<Appointment> {
    const body = {
      appointmentDate: payload.appointmentDate,
      appointmentEndDate: payload.appointmentEndDate ?? null,
      status: toBackendStatus(payload.status) ?? "SCHEDULED",
      type: payload.type,
      notes: payload.notes ?? null,
      psychologistId: Number(payload.psychologistId),
      patientIds: payload.patientIds.map((value) => Number(value)),
      paymentId:
        payload.paymentId === null || payload.paymentId === undefined ? null : Number(payload.paymentId),
    }

    const created = (await api("/appointments", {
      method: "POST",
      body,
    })) as AppointmentDTO

    const mapped = mapDtoToAppointment(created)
    this.cache = this.sortAppointments([...this.cache, mapped])
    return mapped
  }

  async updateAppointment(id: string, payload: UpdateAppointmentPayload): Promise<Appointment> {
    const body: Record<string, unknown> = {}

    if (payload.appointmentDate !== undefined) {
      body.appointmentDate = payload.appointmentDate
    }

    if (payload.appointmentEndDate !== undefined) {
      body.appointmentEndDate = payload.appointmentEndDate
    }

    const backendStatus = toBackendStatus(payload.status)
    if (backendStatus) {
      body.status = backendStatus
    }

    if (payload.type !== undefined) {
      body.type = payload.type
    }

    if (payload.notes !== undefined) {
      body.notes = payload.notes ?? null
    }

    if (payload.psychologistId !== undefined) {
      body.psychologistId = Number(payload.psychologistId)
    }

    if (payload.patientIds !== undefined) {
      body.patientIds = payload.patientIds.map((value) => Number(value))
    }

    if (payload.paymentId !== undefined) {
      body.paymentId = payload.paymentId === null ? null : Number(payload.paymentId)
    }

    const updated = (await api(`/appointments/${id}`, {
      method: "PUT",
      body,
    })) as AppointmentDTO

    const mapped = mapDtoToAppointment(updated)
    this.cache = this.sortAppointments(this.cache.map((appointment) => (appointment.id === id ? mapped : appointment)))
    return mapped
  }

  async deleteAppointment(id: string): Promise<void> {
    await api(`/appointments/${id}`, { method: "DELETE" })
    this.cache = this.cache.filter((appointment) => appointment.id !== id)
  }

  clearCache(): void {
    this.cache = []
    this.loaded = false
  }
}
