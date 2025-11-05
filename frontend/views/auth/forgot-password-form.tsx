"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Brain, ArrowLeft, CheckCircle } from "lucide-react"
import { AuthController } from "@/controllers/auth-controller"
import type { AuthModeChangeHandler } from "./auth-view"

interface ForgotPasswordFormProps {
  onModeChange: AuthModeChangeHandler
}

export function ForgotPasswordForm({ onModeChange }: ForgotPasswordFormProps) {
  const [email, setEmail] = useState("")
  const [errors, setErrors] = useState<{ email?: string }>({})
  const [isLoading, setIsLoading] = useState(false)
  const [isSuccess, setIsSuccess] = useState(false)
  const [resetToken, setResetToken] = useState<string | null>(null)

  const authController = AuthController.getInstance()

  const validateForm = () => {
    const newErrors: { email?: string } = {}

    if (!email) {
      newErrors.email = "Email é obrigatório"
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = "Email inválido"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) return

    setIsLoading(true)
    const result = await authController.forgotPassword(email)
    setIsLoading(false)

    if (result.success) {
      setIsSuccess(true)
      setResetToken(result.token ?? null)
    } else {
      setErrors({ email: result.error })
    }
  }

  if (isSuccess) {
    return (
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-green-100 dark:bg-green-900">
            <CheckCircle className="h-6 w-6 text-green-600 dark:text-green-400" />
          </div>
          <CardTitle className="text-2xl font-bold text-green-600 dark:text-green-400">Email Enviado!</CardTitle>
          <CardDescription>Enviamos as instruções de recuperação para seu email</CardDescription>
        </CardHeader>
        <CardContent>
          <Alert className="mb-4 border-green-200 bg-green-50 dark:border-green-800 dark:bg-green-950">
            <AlertDescription className="text-sm text-green-800 dark:text-green-200">
              Verifique sua caixa de entrada e siga as instruções para redefinir sua senha.
              {resetToken && (
                <>
                  <br />
                  <span className="font-medium">Token gerado:</span> {resetToken}
                </>
              )}
            </AlertDescription>
          </Alert>

          <div className="flex flex-col gap-2">
            <Button
              type="button"
              variant="outline"
              className="w-full bg-transparent"
              onClick={() => onModeChange("login")}
            >
              <ArrowLeft className="mr-2 h-4 w-4" />
              Voltar ao Login
            </Button>
            <Button
              type="button"
              className="w-full bg-blue-600 hover:bg-blue-700"
              onClick={() => onModeChange("reset-password", resetToken ?? undefined)}
            >
              Usar Token Agora
            </Button>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="w-full max-w-md">
      <CardHeader className="text-center">
        <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-blue-100 dark:bg-blue-900">
          <Brain className="h-6 w-6 text-blue-600 dark:text-blue-400" />
        </div>
        <CardTitle className="text-2xl font-bold">Recuperar Senha</CardTitle>
        <CardDescription>Digite seu email para receber as instruções de recuperação</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              placeholder="seu@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className={errors.email ? "border-red-500" : ""}
            />
            {errors.email && <p className="text-sm text-red-600">{errors.email}</p>}
          </div>

          <div className="flex gap-2">
            <Button
              type="button"
              variant="outline"
              className="flex-1 bg-transparent"
              onClick={() => onModeChange("login")}
            >
              <ArrowLeft className="mr-2 h-4 w-4" />
              Voltar
            </Button>
            <Button type="submit" className="flex-1 bg-blue-600 hover:bg-blue-700" disabled={isLoading}>
              {isLoading ? "Enviando..." : "Enviar Email"}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
