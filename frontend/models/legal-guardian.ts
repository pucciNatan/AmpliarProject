export interface LegalGuardian {
  id: string
  fullName: string
  cpf: string
  phoneNumber: string
  patientIds: string[]
}

export interface CreateLegalGuardianPayload {
  fullName: string
  cpf: string
  phoneNumber: string
  patientIds: string[]
}

export type UpdateLegalGuardianPayload = Partial<CreateLegalGuardianPayload>
