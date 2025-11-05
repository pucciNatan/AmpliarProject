import { api } from "@/lib/api-client"
import type { Psychologist, PsychologistWorkingHour } from "@/models/psychologist"

interface PsychologistWorkingHourDTO {
  dayOfWeek: string
  startTime: string | null
  endTime: string | null
  enabled: boolean
}

interface PsychologistDTO {
  id: number
  fullName: string
  cpf: string
  phoneNumber: string
  email: string
  crp?: string | null
  address?: string | null
  bio?: string | null
  specialties?: string[] | null
  workingHours?: PsychologistWorkingHourDTO[] | null
}

interface UpdatePsychologistPayload {
  fullName?: string
  cpf?: string
  phoneNumber?: string
  email?: string
  password?: string
  crp?: string | null
  address?: string | null
  bio?: string | null
  specialties?: string[]
  workingHours?: PsychologistWorkingHour[]
}

const normalizeTime = (value: string | null | undefined): string | null => {
  if (!value) return null
  return value.slice(0, 5)
}

const mapWorkingHourDto = (dto: PsychologistWorkingHourDTO): PsychologistWorkingHour => ({
  dayOfWeek: dto.dayOfWeek?.toLowerCase() ?? "",
  startTime: normalizeTime(dto.startTime),
  endTime: normalizeTime(dto.endTime),
  enabled: Boolean(dto.enabled),
})

const mapDtoToPsychologist = (dto: PsychologistDTO): Psychologist => ({
  id: dto.id.toString(),
  fullName: dto.fullName,
  email: dto.email,
  cpf: dto.cpf,
  phoneNumber: dto.phoneNumber,
  crp: dto.crp ?? null,
  address: dto.address ?? null,
  bio: dto.bio ?? null,
  specialties: dto.specialties ?? [],
  workingHours: dto.workingHours?.map(mapWorkingHourDto) ?? [],
})

export class PsychologistController {
  private static instance: PsychologistController

  static getInstance(): PsychologistController {
    if (!PsychologistController.instance) {
      PsychologistController.instance = new PsychologistController()
    }

    return PsychologistController.instance
  }

  async getPsychologists(): Promise<Psychologist[]> {
    const response = (await api("/psychologists", { method: "GET" })) as PsychologistDTO[]
    return response.map(mapDtoToPsychologist)
  }

  async getPsychologistById(id: string): Promise<Psychologist> {
    const response = (await api(`/psychologists/${id}`, { method: "GET" })) as PsychologistDTO
    return mapDtoToPsychologist(response)
  }

  async updatePsychologist(id: string, payload: UpdatePsychologistPayload): Promise<Psychologist> {
    const body: Record<string, unknown> = {}

    if (payload.fullName !== undefined) body.fullName = payload.fullName
    if (payload.cpf !== undefined) body.cpf = payload.cpf
    if (payload.phoneNumber !== undefined) body.phoneNumber = payload.phoneNumber
    if (payload.email !== undefined) body.email = payload.email
    if (payload.password !== undefined) body.password = payload.password
    if (payload.crp !== undefined) body.crp = payload.crp
    if (payload.address !== undefined) body.address = payload.address
    if (payload.bio !== undefined) body.bio = payload.bio
    if (payload.specialties !== undefined) body.specialties = payload.specialties
    if (payload.workingHours !== undefined) {
      body.workingHours = payload.workingHours.map((slot) => ({
        dayOfWeek: slot.dayOfWeek,
        startTime: slot.startTime ?? null,
        endTime: slot.endTime ?? null,
        enabled: slot.enabled,
      }))
    }

    const response = (await api(`/psychologists/${id}`, {
      method: "PUT",
      body,
    })) as PsychologistDTO

    return mapDtoToPsychologist(response)
  }
}

export type { UpdatePsychologistPayload }
