import { api } from "@/lib/api-client"
import type {
  LegalGuardian,
  CreateLegalGuardianPayload,
  UpdateLegalGuardianPayload,
} from "@/models/legal-guardian"

// DTO do Backend
interface LegalGuardianDTO {
  id: number
  fullName: string
  cpf: string
  phoneNumber: string
  patientIds: number[]
}

const mapDtoToGuardian = (dto: LegalGuardianDTO): LegalGuardian => ({
  id: dto.id.toString(),
  fullName: dto.fullName,
  cpf: dto.cpf,
  phoneNumber: dto.phoneNumber,
  patientIds: dto.patientIds.map(String),
})

export class LegalGuardianController {
  private static instance: LegalGuardianController

  static getInstance(): LegalGuardianController {
    if (!LegalGuardianController.instance) {
      LegalGuardianController.instance = new LegalGuardianController()
    }
    return LegalGuardianController.instance
  }

  async getGuardians(): Promise<LegalGuardian[]> {
    const response = (await api("/guardians", { method: "GET" })) as LegalGuardianDTO[]
    return response.map(mapDtoToGuardian)
  }

  async createGuardian(payload: CreateLegalGuardianPayload): Promise<LegalGuardian> {
    const body = {
      ...payload,
      patientIds: payload.patientIds.map(Number),
    }
    const response = (await api("/guardians", {
      method: "POST",
      body,
    })) as LegalGuardianDTO
    return mapDtoToGuardian(response)
  }

  async updateGuardian(id: string, payload: UpdateLegalGuardianPayload): Promise<LegalGuardian> {
    const body: Record<string, unknown> = {}
    if (payload.fullName !== undefined) body.fullName = payload.fullName
    if (payload.cpf !== undefined) body.cpf = payload.cpf
    if (payload.phoneNumber !== undefined) body.phoneNumber = payload.phoneNumber
    if (payload.patientIds !== undefined) body.patientIds = payload.patientIds.map(Number)

    const response = (await api(`/guardians/${id}`, {
      method: "PUT",
      body,
    })) as LegalGuardianDTO
    return mapDtoToGuardian(response)
  }

  async deleteGuardian(id: string): Promise<void> {
    await api(`/guardians/${id}`, { method: "DELETE" })
  }
}
