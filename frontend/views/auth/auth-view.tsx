"use client"

import { useState } from "react"
import { LoginForm } from "./login-form"
import { RegisterForm } from "./register-form"
import { ForgotPasswordForm } from "./forgot-password-form"
import { ResetPasswordForm } from "./reset-password-form"

interface AuthViewProps {
  onLogin: () => void
}

export type AuthMode = "login" | "register" | "forgot-password" | "reset-password"
export type AuthModeChangeHandler = (mode: AuthMode, token?: string) => void

export function AuthView({ onLogin }: AuthViewProps) {
  const [mode, setMode] = useState<AuthMode>("login")
  const [resetToken, setResetToken] = useState<string>("")

  const handleModeChange: AuthModeChangeHandler = (nextMode, token) => {
    if (token !== undefined) {
      setResetToken(token)
    }
    setMode(nextMode)
  }

  const renderForm = () => {
    switch (mode) {
      case "login":
        return <LoginForm onLogin={onLogin} onModeChange={handleModeChange} />
      case "register":
        return <RegisterForm onLogin={onLogin} onModeChange={handleModeChange} />
      case "forgot-password":
        return <ForgotPasswordForm onModeChange={handleModeChange} />
      case "reset-password":
        return <ResetPasswordForm token={resetToken} onBack={() => handleModeChange("login")} />
      default:
        return <LoginForm onLogin={onLogin} onModeChange={handleModeChange} />
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-green-50 dark:from-gray-900 dark:to-gray-800 p-4">
      {renderForm()}
    </div>
  )
}
