"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Bell, Shield, Palette, Database, Save } from "lucide-react"

export function Settings() {
  const [settings, setSettings] = useState({
    notifications: {
      emailReminders: true,
      smsReminders: false,
      appointmentConfirmations: true,
      paymentReminders: true,
    },
    appearance: {
      theme: "light",
      language: "pt-BR",
    },
    system: {
      autoBackup: true,
      sessionTimeout: "30",
      defaultAppointmentDuration: "60",
    },
    security: {
      twoFactorAuth: false,
      passwordExpiry: "90",
    },
  })

  const handleSave = () => {
    // Simulate API call
    setTimeout(() => {
      alert("Configurações salvas com sucesso!")
    }, 1000)
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Configurações</h1>
          <p className="text-gray-600">Personalize o sistema conforme suas preferências</p>
        </div>

        <Button onClick={handleSave} className="bg-blue-600 hover:bg-blue-700">
          <Save className="mr-2 h-4 w-4" />
          Salvar Alterações
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
                <p className="text-sm text-gray-600">Receber lembretes de consultas por email</p>
              </div>
              <Switch
                id="email-reminders"
                checked={settings.notifications.emailReminders}
                onCheckedChange={(checked) =>
                  setSettings({
                    ...settings,
                    notifications: { ...settings.notifications, emailReminders: checked },
                  })
                }
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="sms-reminders">Lembretes por SMS</Label>
                <p className="text-sm text-gray-600">Receber lembretes de consultas por SMS</p>
              </div>
              <Switch
                id="sms-reminders"
                checked={settings.notifications.smsReminders}
                onCheckedChange={(checked) =>
                  setSettings({
                    ...settings,
                    notifications: { ...settings.notifications, smsReminders: checked },
                  })
                }
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="appointment-confirmations">Confirmações de Agendamento</Label>
                <p className="text-sm text-gray-600">Notificar sobre novos agendamentos</p>
              </div>
              <Switch
                id="appointment-confirmations"
                checked={settings.notifications.appointmentConfirmations}
                onCheckedChange={(checked) =>
                  setSettings({
                    ...settings,
                    notifications: { ...settings.notifications, appointmentConfirmations: checked },
                  })
                }
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <Label htmlFor="payment-reminders">Lembretes de Pagamento</Label>
                <p className="text-sm text-gray-600">Notificar sobre pagamentos pendentes</p>
              </div>
              <Switch
                id="payment-reminders"
                checked={settings.notifications.paymentReminders}
                onCheckedChange={(checked) =>
                  setSettings({
                    ...settings,
                    notifications: { ...settings.notifications, paymentReminders: checked },
                  })
                }
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
                value={settings.appearance.theme}
                onValueChange={(value) =>
                  setSettings({
                    ...settings,
                    appearance: { ...settings.appearance, theme: value },
                  })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="light">Claro</SelectItem>
                  <SelectItem value="dark">Escuro</SelectItem>
                  <SelectItem value="auto">Automático</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="language">Idioma</Label>
              <Select
                value={settings.appearance.language}
                onValueChange={(value) =>
                  setSettings({
                    ...settings,
                    appearance: { ...settings.appearance, language: value },
                  })
                }
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
                <p className="text-sm text-gray-600">Fazer backup dos dados automaticamente</p>
              </div>
              <Switch
                id="auto-backup"
                checked={settings.system.autoBackup}
                onCheckedChange={(checked) =>
                  setSettings({
                    ...settings,
                    system: { ...settings.system, autoBackup: checked },
                  })
                }
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="session-timeout">Timeout da Sessão (minutos)</Label>
              <Input
                id="session-timeout"
                type="number"
                value={settings.system.sessionTimeout}
                onChange={(e) =>
                  setSettings({
                    ...settings,
                    system: { ...settings.system, sessionTimeout: e.target.value },
                  })
                }
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="appointment-duration">Duração Padrão da Consulta (minutos)</Label>
              <Select
                value={settings.system.defaultAppointmentDuration}
                onValueChange={(value) =>
                  setSettings({
                    ...settings,
                    system: { ...settings.system, defaultAppointmentDuration: value },
                  })
                }
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
                <p className="text-sm text-gray-600">Adicionar uma camada extra de segurança</p>
              </div>
              <Switch
                id="two-factor"
                checked={settings.security.twoFactorAuth}
                onCheckedChange={(checked) =>
                  setSettings({
                    ...settings,
                    security: { ...settings.security, twoFactorAuth: checked },
                  })
                }
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password-expiry">Expiração da Senha (dias)</Label>
              <Select
                value={settings.security.passwordExpiry}
                onValueChange={(value) =>
                  setSettings({
                    ...settings,
                    security: { ...settings.security, passwordExpiry: value },
                  })
                }
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

            <div className="pt-4 border-t">
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
