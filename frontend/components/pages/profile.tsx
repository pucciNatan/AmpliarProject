"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Switch } from "@/components/ui/switch"
import { Badge } from "@/components/ui/badge"
import { User, Mail, Phone, MapPin, Clock, Save, Edit } from "lucide-react"

export function Profile() {
  const [isEditing, setIsEditing] = useState(false)
  const [profile, setProfile] = useState({
    name: "Dra. Maria Silva",
    crp: "CRP 06/123456",
    email: "maria.silva@email.com",
    phone: "(11) 99999-9999",
    address: "Rua das Flores, 123 - São Paulo, SP",
    specialties: ["Terapia Cognitivo-Comportamental", "Psicologia Clínica", "Terapia Familiar"],
    bio: "Psicóloga clínica com mais de 10 anos de experiência em atendimento individual e familiar. Especialista em Terapia Cognitivo-Comportamental.",
    workingHours: {
      monday: { start: "08:00", end: "18:00", enabled: true },
      tuesday: { start: "08:00", end: "18:00", enabled: true },
      wednesday: { start: "08:00", end: "18:00", enabled: true },
      thursday: { start: "08:00", end: "18:00", enabled: true },
      friday: { start: "08:00", end: "17:00", enabled: true },
      saturday: { start: "08:00", end: "12:00", enabled: false },
      sunday: { start: "08:00", end: "12:00", enabled: false },
    },
  })

  const weekDays = [
    { key: "monday", label: "Segunda-feira" },
    { key: "tuesday", label: "Terça-feira" },
    { key: "wednesday", label: "Quarta-feira" },
    { key: "thursday", label: "Quinta-feira" },
    { key: "friday", label: "Sexta-feira" },
    { key: "saturday", label: "Sábado" },
    { key: "sunday", label: "Domingo" },
  ]

  const handleSave = () => {
    // Simulate API call
    setTimeout(() => {
      setIsEditing(false)
      alert("Perfil atualizado com sucesso!")
    }, 1000)
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Meu Perfil</h1>
          <p className="text-gray-600">Gerencie suas informações pessoais e profissionais</p>
        </div>

        <Button
          onClick={() => (isEditing ? handleSave() : setIsEditing(true))}
          className={isEditing ? "bg-green-600 hover:bg-green-700" : "bg-blue-600 hover:bg-blue-700"}
        >
          {isEditing ? (
            <>
              <Save className="mr-2 h-4 w-4" />
              Salvar
            </>
          ) : (
            <>
              <Edit className="mr-2 h-4 w-4" />
              Editar
            </>
          )}
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Profile Info */}
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5" />
                Informações Pessoais
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Nome Completo</Label>
                  <Input
                    id="name"
                    value={profile.name}
                    onChange={(e) => setProfile({ ...profile, name: e.target.value })}
                    disabled={!isEditing}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="crp">CRP</Label>
                  <Input
                    id="crp"
                    value={profile.crp}
                    onChange={(e) => setProfile({ ...profile, crp: e.target.value })}
                    disabled={!isEditing}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    value={profile.email}
                    onChange={(e) => setProfile({ ...profile, email: e.target.value })}
                    disabled={!isEditing}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="phone">Telefone</Label>
                  <Input
                    id="phone"
                    value={profile.phone}
                    onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
                    disabled={!isEditing}
                  />
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="address">Endereço</Label>
                <Input
                  id="address"
                  value={profile.address}
                  onChange={(e) => setProfile({ ...profile, address: e.target.value })}
                  disabled={!isEditing}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="bio">Biografia Profissional</Label>
                <Textarea
                  id="bio"
                  value={profile.bio}
                  onChange={(e) => setProfile({ ...profile, bio: e.target.value })}
                  disabled={!isEditing}
                  rows={4}
                />
              </div>
            </CardContent>
          </Card>

          {/* Working Hours */}
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
                {weekDays.map((day) => {
                  const dayConfig = profile.workingHours[day.key as keyof typeof profile.workingHours]
                  return (
                    <div key={day.key} className="flex items-center justify-between p-3 border rounded-lg">
                      <div className="flex items-center gap-3">
                        <Switch
                          checked={dayConfig.enabled}
                          onCheckedChange={(checked) => {
                            if (isEditing) {
                              setProfile({
                                ...profile,
                                workingHours: {
                                  ...profile.workingHours,
                                  [day.key]: { ...dayConfig, enabled: checked },
                                },
                              })
                            }
                          }}
                          disabled={!isEditing}
                        />
                        <span className="font-medium">{day.label}</span>
                      </div>
                      {dayConfig.enabled && (
                        <div className="flex items-center gap-2">
                          <Input
                            type="time"
                            value={dayConfig.start}
                            onChange={(e) => {
                              if (isEditing) {
                                setProfile({
                                  ...profile,
                                  workingHours: {
                                    ...profile.workingHours,
                                    [day.key]: { ...dayConfig, start: e.target.value },
                                  },
                                })
                              }
                            }}
                            disabled={!isEditing}
                            className="w-24"
                          />
                          <span>às</span>
                          <Input
                            type="time"
                            value={dayConfig.end}
                            onChange={(e) => {
                              if (isEditing) {
                                setProfile({
                                  ...profile,
                                  workingHours: {
                                    ...profile.workingHours,
                                    [day.key]: { ...dayConfig, end: e.target.value },
                                  },
                                })
                              }
                            }}
                            disabled={!isEditing}
                            className="w-24"
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

        {/* Profile Summary */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Resumo do Perfil</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="text-center">
                <div className="h-20 w-20 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
                  <span className="text-2xl font-bold text-green-700">
                    {profile.name
                      .split(" ")
                      .map((n) => n[0])
                      .join("")
                      .slice(0, 2)}
                  </span>
                </div>
                <h3 className="font-semibold text-lg">{profile.name}</h3>
                <p className="text-sm text-gray-600">{profile.crp}</p>
              </div>

              <div className="space-y-3 pt-4 border-t">
                <div className="flex items-center gap-2 text-sm">
                  <Mail className="h-4 w-4 text-gray-400" />
                  <span>{profile.email}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Phone className="h-4 w-4 text-gray-400" />
                  <span>{profile.phone}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <MapPin className="h-4 w-4 text-gray-400" />
                  <span>{profile.address}</span>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Especialidades</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex flex-wrap gap-2">
                {profile.specialties.map((specialty, index) => (
                  <Badge key={index} variant="secondary" className="bg-blue-100 text-blue-800">
                    {specialty}
                  </Badge>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
