import type { LoginCredentials, RegisterData, AuthState } from "@/models/auth"

export class AuthController {
  private static instance: AuthController
  private authState: AuthState = {
    user: null,
    isAuthenticated: false,
    isLoading: false,
    error: null,
  }

  static getInstance(): AuthController {
    if (!AuthController.instance) {
      AuthController.instance = new AuthController()
    }
    return AuthController.instance
  }

  async login(credentials: LoginCredentials): Promise<{ success: boolean; error?: string }> {
    this.authState.isLoading = true
    this.authState.error = null

    // Simulate API call
    return new Promise((resolve) => {
      setTimeout(() => {
        if (credentials.email && credentials.password.length >= 6) {
          this.authState.user = {
            id: "1",
            name: "Dra. Maria Silva",
            email: credentials.email,
            crp: "CRP 06/123456",
            phone: "(11) 99999-9999",
            role: "psychologist",
          }
          this.authState.isAuthenticated = true
          this.authState.isLoading = false
          resolve({ success: true })
        } else {
          this.authState.error = "Credenciais inválidas"
          this.authState.isLoading = false
          resolve({ success: false, error: "Credenciais inválidas" })
        }
      }, 1500)
    })
  }

  async register(data: RegisterData): Promise<{ success: boolean; error?: string }> {
    this.authState.isLoading = true
    this.authState.error = null

    // Simulate API call
    return new Promise((resolve) => {
      setTimeout(() => {
        if (data.password !== data.confirmPassword) {
          this.authState.error = "Senhas não coincidem"
          this.authState.isLoading = false
          resolve({ success: false, error: "Senhas não coincidem" })
          return
        }

        this.authState.user = {
          id: "1",
          name: data.name,
          email: data.email,
          crp: data.crp,
          phone: data.phone,
          role: "psychologist",
        }
        this.authState.isAuthenticated = true
        this.authState.isLoading = false
        resolve({ success: true })
      }, 2000)
    })
  }

  async forgotPassword(email: string): Promise<{ success: boolean; error?: string }> {
    this.authState.isLoading = true
    this.authState.error = null

    // Simulate API call
    return new Promise((resolve) => {
      setTimeout(() => {
        this.authState.isLoading = false
        resolve({ success: true })
      }, 1500)
    })
  }

  logout(): void {
    this.authState.user = null
    this.authState.isAuthenticated = false
    this.authState.error = null
  }

  getAuthState(): AuthState {
    return { ...this.authState }
  }
}
