export interface User {
  id: string
  name: string
  email: string
  crp: string
  phone: string
  role: "psychologist" | "secretary" | "admin"
  avatar?: string
}

export interface LoginCredentials {
  email: string
  password: string
}

export interface RegisterData {
  name: string
  email: string
  password: string
  confirmPassword: string
  crp: string
  phone: string
  specialties: string[]
}

export interface AuthState {
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null
}
