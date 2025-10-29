"use client"

import { useState } from "react"
import { LoginForm } from "./login-form"
import { RegisterForm } from "./register-form"
import { ForgotPasswordForm } from "./forgot-password-form"

interface AuthViewProps {
  onLogin: () => void
}

export type AuthMode = "login" | "register" | "forgot-password"

export function AuthView({ onLogin }: AuthViewProps) {
  const [mode, setMode] = useState<AuthMode>("login")

  const renderForm = () => {
    switch (mode) {
      case "login":
        return <LoginForm onLogin={onLogin} onModeChange={setMode} />
      case "register":
        return <RegisterForm onLogin={onLogin} onModeChange={setMode} />
      case "forgot-password":
        return <ForgotPasswordForm onModeChange={setMode} />
      default:
        return <LoginForm onLogin={onLogin} onModeChange={setMode} />
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-green-50 dark:from-gray-900 dark:to-gray-800 p-4">
      {renderForm()}
    </div>
  )
}
