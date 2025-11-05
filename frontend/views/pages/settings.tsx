"use client"

import { useEffect, useMemo, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Bell, Shield, Palette, Database, Save } from "lucide-react"
import { useTheme } from "@/providers/theme-provider"
import { SettingsController } from "@/controllers/settings-controller"
import { AuthController } from "@/controllers/auth-controller"
import type { UserSettings } from "@/models/settings"

export function Settings() {
  const { theme, setTheme } = useTheme()
  const settingsController = useMemo(() => SettingsController.getInstance(), [])
  const authController = useMemo(() => AuthController.getInstance(), [])
  const authState = authController.getAuthState()
  const psychologistId = authState.user?.id ?? null

  const [settings, setSettings] = useState<UserSettings | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  useEffect(() => {
    const load = async () => {
      if (!psychologistId) {
        setIsLoading(false)
        setError("Faça login para gerenciar as configurações")
        return
      }
      setIsLoading(true)
      setError(null)
      try {
        const data = await settingsController.getSettings(psychologistId)
        setSettings(data)
        if (data.preferredTheme !== theme) {
          setTheme(data.preferredTheme)
        }
      } catch (err: any) {
        setError(err.message || "Não foi possível carregar as configurações")
      } finally {
        setIsLoading(false)
      }
    }

    load().catch(() => undefined)
  }, [psychologistId, settingsController, setTheme, theme])

  const handleSave = async () => {
    if (!psychologistId || !settings) return

    setIsSaving(true)
    setError(null)
    setSuccessMessage(null)

    try {
      const updated = await settingsController.updateSettings(psychologistId, settings)
      setSettings(updated)
      if (updated.preferredTheme !== theme) {
        setTheme(updated.preferredTheme)
      }
      setSuccessMessage("Configurações salvas com sucesso")
    } catch (err: any) {
      setError(err.message || "Erro ao salvar configurações")
    } finally {
      setIsSaving(false)
    }
  }

  if (!psychologistId) {
    return (
      <div className="space-y-6">
        <Alert>
          <AlertDescription>Você precisa estar autenticado para acessar as configurações.</AlertDescription>
        </Alert>
      </div>
    )
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Card>
          <CardContent className="py-10 text-center text-sm text-gray-500 dark:text-gray-400">
            Carregando configurações...
          </CardContent>
        </Card>
      </div>
    )
  }

  if (!settings) {
    return (
      <div className="space-y-6">
        <Alert variant="destructive">
          <AlertDescription>{error ?? "Não foi possível carregar as configurações."}</AlertDescription>
        </Alert>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Configurações</h1>
          <p className="text-gray-600 dark:text-gray-400">Personalize o sistema conforme suas preferências</p>
          {error && <p className="mt-2 text-sm text-red-600 dark:text-red-400">{error}</p>}
          {successMessage && <p className="mt-2 text-sm text-green-600 dark:text-green-400">{successMessage}</p>}
        </div>

        <Button
          onClick={handleSave}
          className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
          disabled={isSaving}
        >
          <Save className="mr-2 h-4 w-4" />
          {isSaving ? "Salvando..." : "Salvar Alterações"}
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Notifications */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Bell className="h-5 w-5" />
              Notificações
            </CardTitle>
            <CardDescription>Configure como você deseja receber notificações</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="email-reminders">Lembretes por Email</Label>
                <p className="text-sm text-gray-600 dark:text-gray-400">Receber lembretes de consultas por email</p>
              </div>
              <Switch
                id="email-reminders"
                checked={settings.emailReminders}
                onCheckedChange={(checked) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    emailReminders: checked,
                  })
                }}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="sms-reminders">Lembretes por SMS</Label>
                <p className="text-sm text-gray-600 dark:text-gray-400">Receber lembretes de consultas por SMS</p>
              </div>
              <Switch
                id="sms-reminders"
                checked={settings.smsReminders}
                onCheckedChange={(checked) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    smsReminders: checked,
                  })
                }}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="appointment-confirmations">Confirmações de Agendamento</Label>
                <p className="text-sm text-gray-600 dark:text-gray-400">Notificar sobre novos agendamentos</p>
              </div>
              <Switch
                id="appointment-confirmations"
                checked={settings.appointmentConfirmations}
                onCheckedChange={(checked) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    appointmentConfirmations: checked,
                  })
                }}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="payment-reminders">Lembretes de Pagamento</Label>
                <p className="text-sm text-gray-600 dark:text-gray-400">Notificar sobre pagamentos pendentes</p>
              </div>
              <Switch
                id="payment-reminders"
                checked={settings.paymentReminders}
                onCheckedChange={(checked) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    paymentReminders: checked,
                  })
                }}
              />
            </div>
          </CardContent>
        </Card>

        {/* Appearance */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Palette className="h-5 w-5" />
              Aparência
            </CardTitle>
            <CardDescription>Personalize a aparência do sistema</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="theme">Tema</Label>
              <Select
                value={settings.preferredTheme}
                onValueChange={(value) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    preferredTheme: value as UserSettings["preferredTheme"],
                  })
                }}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="light">Claro</SelectItem>
                  <SelectItem value="dark">Escuro</SelectItem>
                  <SelectItem value="system">Automático</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="language">Idioma</Label>
              <Select
                value={settings.language}
                onValueChange={(value) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    language: value,
                  })
                }}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="pt-BR">Português (Brasil)</SelectItem>
                  <SelectItem value="en-US">English (US)</SelectItem>
                  <SelectItem value="es-ES">Español</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardContent>
        </Card>

        {/* System */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Database className="h-5 w-5" />
              Sistema
            </CardTitle>
            <CardDescription>Configurações gerais do sistema</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="auto-backup">Backup Automático</Label>
                <p className="text-sm text-gray-600 dark:text-gray-400">Fazer backup dos dados automaticamente</p>
              </div>
              <Switch
                id="auto-backup"
                checked={settings.autoBackup}
                onCheckedChange={(checked) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    autoBackup: checked,
                  })
                }}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="session-timeout">Timeout da Sessão (minutos)</Label>
              <Input
                id="session-timeout"
                type="number"
                value={settings.sessionTimeoutMinutes}
                onChange={(e) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    sessionTimeoutMinutes: Number(e.target.value || "0"),
                  })
                }}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="appointment-duration">Duração Padrão da Consulta (minutos)</Label>
              <Select
                value={String(settings.defaultAppointmentDuration)}
                onValueChange={(value) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    defaultAppointmentDuration: Number(value),
                  })
                }}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="30">30 minutos</SelectItem>
                  <SelectItem value="45">45 minutos</SelectItem>
                  <SelectItem value="60">60 minutos</SelectItem>
                  <SelectItem value="90">90 minutos</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardContent>
        </Card>

        {/* Security */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Shield className="h-5 w-5" />
              Segurança
            </CardTitle>
            <CardDescription>Configurações de segurança e privacidade</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="two-factor">Autenticação de Dois Fatores</Label>
                <p className="text-sm text-gray-600 dark:text-gray-400">Adicionar uma camada extra de segurança</p>
              </div>
              <Switch
                id="two-factor"
                checked={settings.twoFactorAuth}
                onCheckedChange={(checked) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    twoFactorAuth: checked,
                  })
                }}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password-expiry">Expiração da Senha (dias)</Label>
              <Select
                value={settings.passwordExpiryDays === 0 ? "never" : String(settings.passwordExpiryDays)}
                onValueChange={(value) => {
                  setSuccessMessage(null)
                  setSettings({
                    ...settings,
                    passwordExpiryDays: value === "never" ? 0 : Number(value),
                  })
                }}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="30">30 dias</SelectItem>
                  <SelectItem value="60">60 dias</SelectItem>
                  <SelectItem value="90">90 dias</SelectItem>
                  <SelectItem value="never">Nunca</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="pt-4 border-t border-gray-200 dark:border-gray-700">
              <Button variant="outline" className="w-full bg-transparent">
                Alterar Senha
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
