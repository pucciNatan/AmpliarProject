import { api } from "@/lib/api-client"
import type { Patient } from "@/models/patient"
// Remova a linha abaixo, ela causa o erro "Cannot find module":
// import { mapAppointmentPatientToPatient } from "@/models/patient.mapper"

interface PatientDTO {
  id: number
  fullName: string
  cpf: string
  phone: string
  email: string | null // CORREÇÃO: Email pode vir nulo do backend
  birthDate: string
  address: string | null
  notes: string | null
  legalGuardianIds: number[]
  status: "active" | "inactive"
  totalAppointments: number
}

const mapDtoToFrontend = (dto: PatientDTO): Patient => {
  return {
    id: dto.id.toString(),
    name: dto.fullName,
    cpf: dto.cpf,
    phone: dto.phone,
    email: dto.email ?? "", // CORREÇÃO: Garantir que o valor seja "" se for nulo
    birthDate: dto.birthDate,
    address: dto.address ?? undefined,
    status: dto.status,
    lastAppointment: undefined,
    totalAppointments: dto.totalAppointments,
    legalGuardianIds: (dto.legalGuardianIds ?? []).map(String),
    notes: dto.notes ?? undefined,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  }
}

export type PatientCreatePayload = Omit<Patient, "id" | "createdAt" | "updatedAt" | "status" | "totalAppointments" | "lastAppointment">
export type PatientUpdatePayload = Partial<PatientCreatePayload>

export class PatientController {
  private static instance: PatientController
  private cache: Patient[] = []
  private loaded = false
  private loadingPromise: Promise<Patient[]> | null = null

  static getInstance(): PatientController {
    if (!PatientController.instance) {
      PatientController.instance = new PatientController()
    }
    return PatientController.instance
  }

  private async loadPatients(force = false): Promise<Patient[]> {
    if (this.loaded && !force) {
      return this.cache
    }

    if (this.loadingPromise && !force) {
      return this.loadingPromise
    }

    this.loadingPromise = (async () => {
      const patientDtos = (await api("/patients", { method: "GET" })) as PatientDTO[]
      this.cache = patientDtos.map(mapDtoToFrontend)
      this.loaded = true
      return this.cache
    })()

    try {
      return await this.loadingPromise
    } finally {
      this.loadingPromise = null
    }
  }

  async getPatients(options: { force?: boolean } = {}): Promise<Patient[]> {
    return this.loadPatients(!!options.force)
  }

  async createPatient(data: PatientCreatePayload): Promise<Patient> {
    const payload = {
      fullName: data.name,
      cpf: data.cpf,
      phoneNumber: data.phone,
      birthDate: data.birthDate,
      email: data.email || null, // Envia nulo se for string vazia
      address: data.address || null, // Envia nulo se for string vazia
      notes: data.notes || null, // Envia nulo se for string vazia
      legalGuardianIds: (data.legalGuardianIds ?? []).map(Number),
    }

    const newPatientDto = (await api("/patients", {
      method: "POST",
      body: payload,
    })) as PatientDTO

    const newPatient = mapDtoToFrontend(newPatientDto)
    this.cache = [newPatient, ...this.cache]
    return newPatient
  }

  async updatePatient(id: string, data: PatientUpdatePayload): Promise<Patient> {
    const payload = {
      fullName: data.name,
      cpf: data.cpf,
      phoneNumber: data.phone,
      birthDate: data.birthDate,
      email: data.email || null, // Envia nulo se for string vazia
      address: data.address || null, // Envia nulo se for string vazia
      notes: data.notes || null, // Envia nulo se for string vazia
      legalGuardianIds: (data.legalGuardianIds ?? []).map(Number),
    }

    const updatedPatientDto = (await api(`/patients/${id}`, {
      method: "PUT",
      body: payload,
    })) as PatientDTO

    const updatedPatient = mapDtoToFrontend(updatedPatientDto)
    this.cache = this.cache.map((p) => (p.id === id ? updatedPatient : p))
    return updatedPatient
  }

  async deletePatient(id: string): Promise<void> {
    await api(`/patients/${id}`, { method: "DELETE" })
    this.cache = this.cache.filter((p) => p.id !== id)
  }

  async getPatientMap(): Promise<Map<string, Patient>> {
    const patients = await this.getPatients()
    return new Map(patients.map((p) => [p.id, p]))
  }

  async getPatientAsOptions(): Promise<{ label: string; value: string }[]> {
    const patients = await this.getPatients()
    return patients
      .filter((p) => p.status === "active")
      .map((p) => ({
        label: p.name,
        value: p.id,
      }))
  }

  async searchPatient(name: string): Promise<Patient[]> {
    const patients = await this.getPatients()
    return patients.filter(
      (p) =>
        p.status === "active" &&
        p.name.toLowerCase().includes(name.toLowerCase())
    )
  }

  clearCache(): void {
    this.cache = []
    this.loaded = false
  }
}

export const patientController = PatientController.getInstance()
