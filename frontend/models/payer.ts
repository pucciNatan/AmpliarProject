export interface Payer {
  id: string
  fullName: string
  cpf: string
  phoneNumber: string
}
export interface CreatePayerPayload {
  fullName: string
  cpf: string
  phoneNumber: string
}

export type UpdatePayerPayload = Partial<CreatePayerPayload>
