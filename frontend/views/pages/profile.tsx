"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Switch } from "@/components/ui/switch"
import { Badge } from "@/components/ui/badge"
import { User, Mail, Phone, MapPin, Clock, Save, Edit, RefreshCcw } from "lucide-react"
import { PsychologistController, type UpdatePsychologistPayload } from "@/controllers/psychologist-controller"
import { AuthController } from "@/controllers/auth-controller"
import type { Psychologist, PsychologistWorkingHour } from "@/models/psychologist"

type WorkingHoursState = Record<
  string,
  {
    start: string
    end: string
    enabled: boolean
  }
>

const WEEK_DAYS: Array<{ key: string; label: string }> = [
  { key: "monday", label: "Segunda-feira" },
  { key: "tuesday", label: "Terça-feira" },
  { key: "wednesday", label: "Quarta-feira" },
  { key: "thursday", label: "Quinta-feira" },
  { key: "friday", label: "Sexta-feira" },
  { key: "saturday", label: "Sábado" },
  { key: "sunday", label: "Domingo" },
]

const createDefaultWorkingHours = (): WorkingHoursState =>
  WEEK_DAYS.reduce((acc, day) => {
    acc[day.key] = { start: "08:00", end: "17:00", enabled: false }
    return acc
  }, {} as WorkingHoursState)

const sanitizePhone = (value: string) => value.replace(/\D/g, "")

const mapWorkingHoursToState = (workingHours?: PsychologistWorkingHour[] | null): WorkingHoursState => {
  const base = createDefaultWorkingHours()
  if (!workingHours) {
    return base
  }

  const mapped = { ...base }
  workingHours.forEach((slot) => {
    const key = slot.dayOfWeek?.toLowerCase() ?? ""
    if (!key || !mapped[key]) return
    mapped[key] = {
      start: slot.startTime ?? "08:00",
      end: slot.endTime ?? "17:00",
      enabled: slot.enabled,
    }
  })

  return mapped
}

const mapStateToWorkingHours = (state: WorkingHoursState): PsychologistWorkingHour[] =>
  WEEK_DAYS.map(({ key }) => ({
    dayOfWeek: key,
    startTime: state[key].start || null,
    endTime: state[key].end || null,
    enabled: state[key].enabled,
  }))

export function Profile() {
  const authController = useMemo(() => AuthController.getInstance(), [])
  const psychologistController = useMemo(() => PsychologistController.getInstance(), [])
  const authState = authController.getAuthState()
  const psychologistId = authState.user?.id ?? null

  const [isEditing, setIsEditing] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const [profile, setProfile] = useState<{
    name: string
    crp: string
    email: string
    phone: string
    address: string
    bio: string
    specialties: string[]
    cpf: string
  } | null>(null)

  const [specialtiesInput, setSpecialtiesInput] = useState("")
  const [workingHours, setWorkingHours] = useState<WorkingHoursState>(() => createDefaultWorkingHours())

  const loadProfile = useCallback(async () => {
    if (!psychologistId) {
      setError("Não foi possível identificar o usuário autenticado.")
      setIsLoading(false)
      return
    }

    setIsLoading(true)
    setError(null)
    try {
      const data = await psychologistController.getPsychologistById(psychologistId)
      setProfile({
        name: data.fullName,
        crp: data.crp ?? "",
        email: data.email,
        phone: data.phoneNumber,
        address: data.address ?? "",
        bio: data.bio ?? "",
        specialties: data.specialties ?? [],
        cpf: data.cpf,
      })
      setSpecialtiesInput((data.specialties ?? []).join(", "))
      setWorkingHours(mapWorkingHoursToState(data.workingHours))
      setSuccessMessage(null)
    } catch (err) {
      const message = err instanceof Error ? err.message : "Não foi possível carregar o perfil."
      setError(message)
    } finally {
      setIsLoading(false)
    }
  }, [psychologistController, psychologistId])

  useEffect(() => {
    void loadProfile()
  }, [loadProfile])

  const handleSave = async () => {
    if (!profile || !psychologistId) return

    setIsSaving(true)
    setError(null)
    try {
      const specialties = specialtiesInput
        .split(",")
        .map((item) => item.trim())
        .filter((item) => item.length > 0)

      const payload: UpdatePsychologistPayload = {
        fullName: profile.name,
        email: profile.email,
        phoneNumber: sanitizePhone(profile.phone),
        crp: profile.crp || null,
        address: profile.address || null,
        bio: profile.bio || null,
        specialties,
        workingHours: mapStateToWorkingHours(workingHours),
      }

      if (profile.cpf) {
        payload.cpf = profile.cpf
      }

      const updated = await psychologistController.updatePsychologist(psychologistId, payload)

      setProfile({
        name: updated.fullName,
        crp: updated.crp ?? "",
        email: updated.email,
        phone: updated.phoneNumber,
        address: updated.address ?? "",
        bio: updated.bio ?? "",
        specialties: updated.specialties ?? [],
        cpf: updated.cpf,
      })
      setSpecialtiesInput((updated.specialties ?? []).join(", "))
      setWorkingHours(mapWorkingHoursToState(updated.workingHours))
      setIsEditing(false)
      setSuccessMessage("Perfil atualizado com sucesso.")
    } catch (err) {
      const message = err instanceof Error ? err.message : "Erro ao atualizar o perfil."
      setError(message)
    } finally {
      setIsSaving(false)
    }
  }

  const handleCancelEdit = () => {
    setIsEditing(false)
    setSuccessMessage(null)
    void loadProfile()
  }

  if (!psychologistId) {
    return (
      <div className="space-y-6">
        <Card>
          <CardContent className="py-10 text-center text-sm text-gray-500 dark:text-gray-400">
            Faça login para visualizar e editar seu perfil profissional.
          </CardContent>
        </Card>
      </div>
    )
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Card>
          <CardContent className="py-10 text-center text-sm text-gray-500 dark:text-gray-400">
            Carregando informações do perfil...
          </CardContent>
        </Card>
      </div>
    )
  }

  if (!profile) {
    return (
      <div className="space-y-6">
        <Card>
          <CardContent className="py-10 text-center text-sm text-gray-500 dark:text-gray-400">
            {error ?? "Não foi possível carregar os dados do perfil."}
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Meu Perfil</h1>
          <p className="text-gray-600 dark:text-gray-400">
            Gerencie suas informações pessoais e profissionais no sistema
          </p>
          {error && <p className="mt-2 text-sm text-red-600 dark:text-red-400">{error}</p>}
          {successMessage && <p className="mt-2 text-sm text-green-600 dark:text-green-400">{successMessage}</p>}
        </div>

        <div className="flex gap-2">
          {isEditing && (
            <Button variant="outline" onClick={handleCancelEdit} disabled={isSaving}>
              <RefreshCcw className="mr-2 h-4 w-4" />
              Descartar
            </Button>
          )}
          <Button
            onClick={() => {
              if (isEditing) {
                void handleSave()
              } else {
                setIsEditing(true)
                setSuccessMessage(null)
              }
            }}
            className={
              isEditing
                ? "bg-green-600 hover:bg-green-700 dark:bg-green-500 dark:hover:bg-green-600"
                : "bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
            }
            disabled={isSaving}
          >
            {isEditing ? (
              <>
                <Save className="mr-2 h-4 w-4" />
                {isSaving ? "Salvando..." : "Salvar"}
              </>
            ) : (
              <>
                <Edit className="mr-2 h-4 w-4" />
                Editar
              </>
            )}
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                Informações Pessoais
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Nome Completo</Label>
                  <Input
                    id="name"
                    value={profile.name}
                    onChange={(event) => setProfile({ ...profile, name: event.target.value })}
                    disabled={!isEditing}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="crp">CRP</Label>
                  <Input
                    id="crp"
                    value={profile.crp}
                    onChange={(event) => setProfile({ ...profile, crp: event.target.value })}
                    disabled={!isEditing}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    value={profile.email}
                    onChange={(event) => setProfile({ ...profile, email: event.target.value })}
                    disabled={!isEditing}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="phone">Telefone</Label>
                  <Input
                    id="phone"
                    value={profile.phone}
                    onChange={(event) => setProfile({ ...profile, phone: event.target.value })}
                    disabled={!isEditing}
                  />
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="address">Endereço</Label>
                <Input
                  id="address"
                  value={profile.address}
                  onChange={(event) => setProfile({ ...profile, address: event.target.value })}
                  disabled={!isEditing}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="bio">Biografia Profissional</Label>
                <Textarea
                  id="bio"
                  rows={4}
                  value={profile.bio}
                  onChange={(event) => setProfile({ ...profile, bio: event.target.value })}
                  disabled={!isEditing}
                />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Clock className="h-5 w-5" />
                Horários de Atendimento
              </CardTitle>
              <CardDescription>Configure seus horários disponíveis para agendamento</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {WEEK_DAYS.map((day) => {
                  const config = workingHours[day.key]
                  return (
                    <div
                      key={day.key}
                      className="flex flex-col gap-3 rounded-lg border border-gray-200 p-3 dark:border-gray-700 md:flex-row md:items-center md:justify-between"
                    >
                      <div className="flex items-center gap-3">
                        <Switch
                          checked={config.enabled}
                          onCheckedChange={(checked) => {
                            if (!isEditing) return
                            setWorkingHours((prev) => ({
                              ...prev,
                              [day.key]: {
                                ...prev[day.key],
                                enabled: checked,
                              },
                            }))
                          }}
                          disabled={!isEditing}
                        />
                        <span className="font-medium">{day.label}</span>
                      </div>
                      {config.enabled && (
                        <div className="flex items-center gap-2 md:justify-end">
                          <Input
                            type="time"
                            value={config.start}
                            onChange={(event) => {
                              if (!isEditing) return
                              const value = event.target.value
                              setWorkingHours((prev) => ({
                                ...prev,
                                [day.key]: {
                                  ...prev[day.key],
                                  start: value,
                                },
                              }))
                            }}
                            className="w-24"
                            disabled={!isEditing}
                          />
                          <span>às</span>
                          <Input
                            type="time"
                            value={config.end}
                            onChange={(event) => {
                              if (!isEditing) return
                              const value = event.target.value
                              setWorkingHours((prev) => ({
                                ...prev,
                                [day.key]: {
                                  ...prev[day.key],
                                  end: value,
                                },
                              }))
                            }}
                            className="w-24"
                            disabled={!isEditing}
                          />
                        </div>
                      )}
                    </div>
                  )
                })}
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Resumo do Perfil</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="text-center">
                <div className="mx-auto mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-green-100 text-2xl font-bold text-green-700 dark:bg-green-900 dark:text-green-200">
                  {profile.name
                    .split(" ")
                    .filter(Boolean)
                    .map((part) => part[0])
                    .join("")
                    .slice(0, 2)}
                </div>
                <h3 className="font-semibold text-lg text-gray-900 dark:text-gray-100">{profile.name}</h3>
                <p className="text-sm text-gray-600 dark:text-gray-400">{profile.crp || "CRP não informado"}</p>
              </div>

              <div className="space-y-3 border-t border-gray-200 pt-4 text-sm dark:border-gray-700">
                <div className="flex items-center gap-2">
                  <Mail className="h-4 w-4 text-gray-400" />
                  <span>{profile.email}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Phone className="h-4 w-4 text-gray-400" />
                  <span>{profile.phone}</span>
                </div>
                <div className="flex items-center gap-2">
                  <MapPin className="h-4 w-4 text-gray-400" />
                  <span>{profile.address || "Endereço não informado"}</span>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Especialidades</CardTitle>
              <CardDescription>Defina as áreas de atuação que deseja destacar aos pacientes</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              {isEditing ? (
                <Textarea
                  value={specialtiesInput}
                  onChange={(event) => setSpecialtiesInput(event.target.value)}
                  placeholder="Separe as especialidades por vírgula"
                  rows={3}
                />
              ) : (
                <div className="flex flex-wrap gap-2">
                  {(profile.specialties.length ? profile.specialties : ["Nenhuma especialidade informada"]).map(
                    (specialty, index) => (
                      <Badge key={index} className="bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200">
                        {specialty}
                      </Badge>
                    ),
                  )}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
