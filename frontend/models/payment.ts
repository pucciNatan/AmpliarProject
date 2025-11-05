export interface Payment {
  id: string
  amount: number
  paymentDate: string
  payerId: string
  payerName?: string | null
}

export interface CreatePaymentPayload {
  amount: number
  paymentDate: string
  payerId: string
}

export type UpdatePaymentPayload = Partial<CreatePaymentPayload>
