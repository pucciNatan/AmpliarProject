"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { CalendarIcon, Plus, Clock, User, ChevronLeft, ChevronRight } from "lucide-react"

interface Appointment {
  id: number
  patientName: string
  psychologist: string
  date: string
  time: string
  type: string
  status: "scheduled" | "completed" | "cancelled" | "no-show"
  notes?: string
}

export function Appointments() {
  const [currentDate, setCurrentDate] = useState(new Date())
  const [viewMode, setViewMode] = useState<"day" | "week" | "month">("week")
  const [isNewAppointmentOpen, setIsNewAppointmentOpen] = useState(false)

  const appointments: Appointment[] = [
    {
      id: 1,
      patientName: "Ana Carolina Santos",
      psychologist: "Dra. Maria Silva",
      date: "2024-01-20",
      time: "09:00",
      type: "Consulta Individual",
      status: "scheduled",
      notes: "Primeira consulta",
    },
    {
      id: 2,
      patientName: "João Pedro Silva",
      psychologist: "Dra. Maria Silva",
      date: "2024-01-20",
      time: "10:30",
      type: "Terapia Cognitiva",
      status: "scheduled",
    },
    {
      id: 3,
      patientName: "Maria Fernanda Costa",
      psychologist: "Dra. Maria Silva",
      date: "2024-01-20",
      time: "14:00",
      type: "Avaliação Inicial",
      status: "completed",
    },
  ]

  const getStatusBadge = (status: Appointment["status"]) => {
    const statusConfig = {
      scheduled: { label: "Agendado", className: "bg-blue-100 text-blue-800" },
      completed: { label: "Concluído", className: "bg-green-100 text-green-800" },
      cancelled: { label: "Cancelado", className: "bg-red-100 text-red-800" },
      "no-show": { label: "Faltou", className: "bg-yellow-100 text-yellow-800" },
    }

    const config = statusConfig[status]
    return <Badge className={config.className}>{config.label}</Badge>
  }

  const timeSlots = [
    "08:00",
    "08:30",
    "09:00",
    "09:30",
    "10:00",
    "10:30",
    "11:00",
    "11:30",
    "14:00",
    "14:30",
    "15:00",
    "15:30",
    "16:00",
    "16:30",
    "17:00",
    "17:30",
    "18:00",
  ]

  const formatDate = (date: Date) => {
    return date.toLocaleDateString("pt-BR", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    })
  }

  const navigateDate = (direction: "prev" | "next") => {
    const newDate = new Date(currentDate)
    if (viewMode === "day") {
      newDate.setDate(newDate.getDate() + (direction === "next" ? 1 : -1))
    } else if (viewMode === "week") {
      newDate.setDate(newDate.getDate() + (direction === "next" ? 7 : -7))
    } else {
      newDate.setMonth(newDate.getMonth() + (direction === "next" ? 1 : -1))
    }
    setCurrentDate(newDate)
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Agendamentos</h1>
          <p className="text-gray-600">Gerencie suas consultas</p>
        </div>

        <Dialog open={isNewAppointmentOpen} onOpenChange={setIsNewAppointmentOpen}>
          <DialogTrigger asChild>
            <Button className="bg-blue-600 hover:bg-blue-700">
              <Plus className="mr-2 h-4 w-4" />
              Nova Consulta
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Agendar Nova Consulta</DialogTitle>
              <DialogDescription>Preencha os dados da consulta</DialogDescription>
            </DialogHeader>
            <div className="grid grid-cols-2 gap-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="patient">Paciente</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o paciente" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="1">Ana Carolina Santos</SelectItem>
                    <SelectItem value="2">João Pedro Silva</SelectItem>
                    <SelectItem value="3">Maria Fernanda Costa</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="psychologist">Psicólogo</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o psicólogo" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="1">Dra. Maria Silva</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="date">Data</Label>
                <Input id="date" type="date" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="time">Horário</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o horário" />
                  </SelectTrigger>
                  <SelectContent>
                    {timeSlots.map((time) => (
                      <SelectItem key={time} value={time}>
                        {time}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="col-span-2 space-y-2">
                <Label htmlFor="type">Tipo de Consulta</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o tipo" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="individual">Consulta Individual</SelectItem>
                    <SelectItem value="cognitive">Terapia Cognitiva</SelectItem>
                    <SelectItem value="family">Terapia Familiar</SelectItem>
                    <SelectItem value="evaluation">Avaliação Inicial</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="col-span-2 space-y-2">
                <Label htmlFor="notes">Observações</Label>
                <Textarea id="notes" placeholder="Observações sobre a consulta..." />
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setIsNewAppointmentOpen(false)}>
                Cancelar
              </Button>
              <Button className="bg-blue-600 hover:bg-blue-700">Agendar</Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {/* Calendar Controls */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2">
                <Button variant="outline" size="sm" onClick={() => navigateDate("prev")}>
                  <ChevronLeft className="h-4 w-4" />
                </Button>
                <Button variant="outline" size="sm" onClick={() => navigateDate("next")}>
                  <ChevronRight className="h-4 w-4" />
                </Button>
              </div>
              <h2 className="text-lg font-semibold">{formatDate(currentDate)}</h2>
            </div>

            <div className="flex gap-2">
              <Button variant={viewMode === "day" ? "default" : "outline"} size="sm" onClick={() => setViewMode("day")}>
                Dia
              </Button>
              <Button
                variant={viewMode === "week" ? "default" : "outline"}
                size="sm"
                onClick={() => setViewMode("week")}
              >
                Semana
              </Button>
              <Button
                variant={viewMode === "month" ? "default" : "outline"}
                size="sm"
                onClick={() => setViewMode("month")}
              >
                Mês
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Appointments List */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CalendarIcon className="h-5 w-5" />
            Consultas do Dia
          </CardTitle>
          <CardDescription>{appointments.length} consulta(s) agendada(s)</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {appointments.map((appointment) => (
              <div
                key={appointment.id}
                className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
              >
                <div className="flex items-center gap-4">
                  <div className="text-center">
                    <div className="text-lg font-semibold text-blue-600">{appointment.time}</div>
                    <div className="text-xs text-gray-500">
                      {new Date(appointment.date).toLocaleDateString("pt-BR", {
                        day: "2-digit",
                        month: "2-digit",
                      })}
                    </div>
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <User className="h-4 w-4 text-gray-400" />
                      <p className="font-medium text-gray-900">{appointment.patientName}</p>
                    </div>
                    <p className="text-sm text-gray-600 mb-1">{appointment.type}</p>
                    {appointment.notes && <p className="text-xs text-gray-500">{appointment.notes}</p>}
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  {getStatusBadge(appointment.status)}
                  <div className="flex gap-1">
                    <Button variant="ghost" size="sm">
                      <Clock className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
