import { api } from "@/lib/api-client"
import type { CreatePaymentPayload, Payment, UpdatePaymentPayload } from "@/models/payment"
import type { Payer, CreatePayerPayload, UpdatePayerPayload } from "@/models/payer"

interface PaymentDTO {
  id: number
  valor: number | string
  paymentDate: string
  payerId: number
}

interface PayerDTO {
  id: number
  fullName: string
  cpf: string
  phoneNumber: string
}

const parseAmount = (valor: number | string): number => {
  if (typeof valor === "number") {
    return valor
  }

  const parsed = Number(valor)
  return Number.isNaN(parsed) ? 0 : parsed
}

const mapPaymentDtoToPayment = (dto: PaymentDTO): Payment => ({
  id: dto.id.toString(),
  amount: parseAmount(dto.valor),
  paymentDate: dto.paymentDate,
  payerId: dto.payerId.toString(),
})

const mapPayerDtoToPayer = (dto: PayerDTO): Payer => ({
  id: dto.id.toString(),
  fullName: dto.fullName,
  cpf: dto.cpf,
  phoneNumber: dto.phoneNumber,
})

export class FinanceController {
  private static instance: FinanceController

  static getInstance(): FinanceController {
    if (!FinanceController.instance) {
      FinanceController.instance = new FinanceController()
    }

    return FinanceController.instance
  }

  async getPayments(): Promise<Payment[]> {
    const response = (await api("/payments", { method: "GET" })) as PaymentDTO[]
    return response.map(mapPaymentDtoToPayment)
  }

  async createPayment(payload: CreatePaymentPayload): Promise<Payment> {
    const body = {
      valor: payload.amount,
      paymentDate: payload.paymentDate,
      payerId: Number(payload.payerId),
    }

    const response = (await api("/payments", {
      method: "POST",
      body,
    })) as PaymentDTO

    return mapPaymentDtoToPayment(response)
  }

  async updatePayment(id: string, payload: UpdatePaymentPayload): Promise<Payment> {
    const body: Record<string, unknown> = {}
    if (payload.amount !== undefined) body.valor = payload.amount
    if (payload.paymentDate !== undefined) body.paymentDate = payload.paymentDate
    if (payload.payerId !== undefined) body.payerId = Number(payload.payerId)

    const response = (await api(`/payments/${id}`, {
      method: "PUT",
      body,
    })) as PaymentDTO

    return mapPaymentDtoToPayment(response)
  }

  async deletePayment(id: string): Promise<void> {
    await api(`/payments/${id}`, { method: "DELETE" })
  }


  async getPayers(): Promise<Payer[]> {
    const response = (await api("/payers", { method: "GET" })) as PayerDTO[]
    return response.map(mapPayerDtoToPayer)
  }

  async createPayer(payload: CreatePayerPayload): Promise<Payer> {
    const response = (await api("/payers", {
      method: "POST",
      body: payload,
    })) as PayerDTO
    return mapPayerDtoToPayer(response)
  }

  async updatePayer(id: string, payload: UpdatePayerPayload): Promise<Payer> {
    const response = (await api(`/payers/${id}`, {
      method: "PUT",
      body: payload,
    })) as PayerDTO
    return mapPayerDtoToPayer(response)
  }

  async deletePayer(id: string): Promise<void> {
    await api(`/payers/${id}`, { method: "DELETE" })
  }
}

export const financeController = FinanceController.getInstance()
