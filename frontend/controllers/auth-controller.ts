import type { LoginCredentials, RegisterData, AuthState, User } from "@/models/auth"

const AUTH_USER_KEY = "ampliar-auth-user"
const AUTH_TOKEN_KEY = "ampliar-auth-token"

export class AuthController {
  private static instance: AuthController
  private authState: AuthState = {
    user: null,
    isAuthenticated: false,
    isLoading: false,
    error: null,
    token: null,
  }

  static getInstance(): AuthController {
    if (!AuthController.instance) {
      AuthController.instance = new AuthController()

      if (typeof window !== "undefined") {
        try {
          const user = localStorage.getItem(AUTH_USER_KEY)
          const token = localStorage.getItem(AUTH_TOKEN_KEY)

          if (user && token) {
            AuthController.instance.authState = {
              user: JSON.parse(user),
              token: token,
              isAuthenticated: true,
              isLoading: false,
              error: null,
            }
          }
        } catch (e) {
          console.error("Falha ao carregar estado de autenticação", e)
          localStorage.removeItem(AUTH_USER_KEY)
          localStorage.removeItem(AUTH_TOKEN_KEY)
        }
      }
    }
    return AuthController.instance
  }

  async login(credentials: LoginCredentials): Promise<{ success: boolean; error?: string }> {
    this.authState.isLoading = true
    this.authState.error = null

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
      })

      if (response.ok) {
        const data = await response.json()
        const user: User = {
          id: data.userId.toString(),
          name: data.fullName,
          email: data.email,
        }

        this.authState.user = user
        this.authState.token = data.token
        this.authState.isAuthenticated = true
        this.authState.isLoading = false

        localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user))
        localStorage.setItem(AUTH_TOKEN_KEY, data.token)

        console.log("Login com sucesso, token armazenado:", this.authState.token)
        return { success: true }

      } else {
        let errorMessage = `Erro ${response.status} ao fazer login.`;
        try {
          const errorData = await response.json();
          errorMessage = errorData.error || errorData.message || "Credenciais inválidas";
        } catch (e) {
          errorMessage = "Credenciais inválidas ou erro no servidor."
        }

        this.authState.error = errorMessage
        this.authState.isLoading = false
        return { success: false, error: errorMessage }
      }

    } catch (error) {
      console.error("Falha na conexão ao logar:", error)
      const errorMessage = "Não foi possível conectar ao servidor. Tente novamente."
      this.authState.error = errorMessage
      this.authState.isLoading = false
      return { success: false, error: errorMessage }
    }
  }

  async register(data: RegisterData): Promise<{ success: boolean; error?: string }> {
    this.authState.isLoading = true
    this.authState.error = null

    const payload = {
      fullName: data.name,
      email: data.email,
      password: data.password,
      cpf: data.cpf,
      phoneNumber: data.phone,
    };

    try {
      const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        this.authState.isLoading = false;
        const loginResult = await this.login({ email: data.email, password: data.password });
        return loginResult;

      } else {
        let errorMessage = `Erro ${response.status} ao registrar.`;
        try {
          const errorData = await response.json();
          errorMessage = errorData.error || errorData.message || JSON.stringify(errorData);
        } catch (jsonError) {
          try {
            const errorText = await response.text();
            if (errorText) {
              errorMessage = errorText;
            } else {
              errorMessage = `Erro ${response.status}: A resposta do servidor estava vazia.`;
            }
          } catch (textError) {
             errorMessage = `Erro ${response.status}: Não foi possível ler a resposta do servidor.`;
          }
        }

        this.authState.error = errorMessage;
        this.authState.isLoading = false;
        return { success: false, error: errorMessage };
      }

    } catch (error) {
      console.error("Falha na conexão ao registrar:", error);
      const errorMessage = "Não foi possível conectar ao servidor. Tente novamente.";
      this.authState.error = errorMessage;
      this.authState.isLoading = false;
      return { success: false, error: errorMessage };
    }
  }

  async forgotPassword(email: string): Promise<{ success: boolean; error?: string }> {
    this.authState.isLoading = true
    this.authState.error = null

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
    this.authState.token = null
    localStorage.removeItem(AUTH_USER_KEY)
    localStorage.removeItem(AUTH_TOKEN_KEY)
  }

  getAuthState(): AuthState {
    return { ...this.authState }
  }
}
