export interface Patient {
  id: string
  name: string
  cpf: string
  phone: string
  birthDate: string
  address?: string
  status: "active" | "inactive"
  lastAppointment?: string
  totalAppointments: number
  responsibleId?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

export interface ResponsibleParty {
  id: string
  name: string
  cpf: string
  phone: string
  email: string
  relationship: string
  patients: string[]
}
