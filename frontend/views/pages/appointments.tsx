"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { CalendarIcon, Plus, User, ChevronLeft, ChevronRight, Pencil, Trash2 } from "lucide-react"
import { AppointmentController } from "@/controllers/appointment-controller"
import { PatientController } from "@/controllers/patient-controller"
import { PsychologistController } from "@/controllers/psychologist-controller"
import { InteractiveCalendar } from "@/views/components/interactive-calendar"
import type { Appointment, CreateAppointmentPayload, UpdateAppointmentPayload } from "@/models/appointment"
import type { Patient } from "@/models/patient"
import type { Psychologist } from "@/models/psychologist"

const formatDateForInput = (date: Date) => date.toISOString().split("T")[0]

const buildLocalDateTime = (date: string, time: string) => `${date}T${time}:00`

const addMinutesToTime = (time: string, minutes: number) => {
  const [hourStr, minuteStr] = time.split(":")
  const baseMinutes = Number(hourStr) * 60 + Number(minuteStr)
  const totalMinutes = baseMinutes + minutes
  const normalizedMinutes = ((totalMinutes % (24 * 60)) + (24 * 60)) % (24 * 60)
  const newHour = Math.floor(normalizedMinutes / 60)
  const newMinute = normalizedMinutes % 60
  return `${newHour.toString().padStart(2, "0")}:${newMinute.toString().padStart(2, "0")}`
}

const appointmentTypes = [
  { label: "Consulta Individual", value: "Consulta Individual" },
  { label: "Terapia Cognitiva", value: "Terapia Cognitiva" },
  { label: "Terapia Familiar", value: "Terapia Familiar" },
  { label: "Avaliação Inicial", value: "Avaliação Inicial" },
]

export function Appointments() {
  const [currentDate, setCurrentDate] = useState(new Date())
  const [viewMode, setViewMode] = useState<"day" | "week" | "month">("week")
  const [isAppointmentDialogOpen, setIsAppointmentDialogOpen] = useState(false)
  const [dialogMode, setDialogMode] = useState<"create" | "edit">("create")
  const [selectedDate, setSelectedDate] = useState<string | null>(null)
  const [appointments, setAppointments] = useState<Appointment[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [patients, setPatients] = useState<Patient[]>([])
  const [psychologists, setPsychologists] = useState<Psychologist[]>([])
  const [formError, setFormError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [editingAppointment, setEditingAppointment] = useState<Appointment | null>(null)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [appointmentToDelete, setAppointmentToDelete] = useState<Appointment | null>(null)
  const [isDeleting, setIsDeleting] = useState(false)
  const [formState, setFormState] = useState(() => ({
    patientId: "",
    psychologistId: "",
    date: formatDateForInput(new Date()),
    time: "",
    type: "",
    notes: "",
  }))

  const appointmentController = useMemo(() => AppointmentController.getInstance(), [])
  const patientController = useMemo(() => PatientController.getInstance(), [])
  const psychologistController = useMemo(() => PsychologistController.getInstance(), [])

  const getDefaultDate = useCallback(() => selectedDate ?? formatDateForInput(currentDate), [selectedDate, currentDate])

  const resetForm = useCallback(() => {
    setFormState({
      patientId: "",
      psychologistId: "",
      date: getDefaultDate(),
      time: "",
      type: "",
      notes: "",
    })
    setFormError(null)
    setIsSubmitting(false)
  }, [getDefaultDate])

  const openCreateDialog = useCallback(() => {
    setDialogMode("create")
    setEditingAppointment(null)
    resetForm()
    setIsAppointmentDialogOpen(true)
  }, [resetForm])

  const openEditDialog = useCallback((appointment: Appointment) => {
    setDialogMode("edit")
    setEditingAppointment(appointment)
    setFormState({
      patientId: appointment.patients[0]?.id ?? "",
      psychologistId: appointment.psychologist?.id ?? "",
      date: appointment.date,
      time: appointment.time ?? "",
      type: appointment.type,
      notes: appointment.notes ?? "",
    })
    setFormError(null)
    setIsSubmitting(false)
    setIsAppointmentDialogOpen(true)
  }, [])

  const closeAppointmentDialog = useCallback(() => {
    setIsAppointmentDialogOpen(false)
    setEditingAppointment(null)
    resetForm()
  }, [resetForm])

  const loadAppointments = useCallback(
    async (force = false) => {
      setIsLoading(true)
      setError(null)
      try {
        const data = await appointmentController.getAppointments({ force })
        setAppointments(data)
      } catch (err) {
        console.error("Erro ao carregar agendamentos", err)
        setError(err instanceof Error ? err.message : "Erro ao carregar agendamentos")
      } finally {
        setIsLoading(false)
      }
    },
    [appointmentController],
  )

  useEffect(() => {
    void loadAppointments()
  }, [loadAppointments])

  useEffect(() => {
    if (isAppointmentDialogOpen && dialogMode === "create" && selectedDate) {
      setFormState((prev) => ({
        ...prev,
        date: selectedDate,
      }))
    }
  }, [isAppointmentDialogOpen, dialogMode, selectedDate])

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const [patientsList, psychologistsList] = await Promise.all([
          patientController.getPatients(),
          psychologistController.getPsychologists(),
        ])
        setPatients(patientsList)
        setPsychologists(psychologistsList)
      } catch (err) {
        console.error("Erro ao carregar dados auxiliares", err)
      }
    }

    void fetchOptions()
  }, [patientController, psychologistController])

  const getStatusBadge = (status: Appointment["status"]) => {
    const statusConfig = {
      scheduled: { label: "Agendado", className: "bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200" },
      completed: { label: "Concluído", className: "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200" },
      cancelled: { label: "Cancelado", className: "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200" },
      "no-show": {
        label: "Faltou",
        className: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200",
      },
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

  const handleSubmitAppointment = async () => {
    setFormError(null)

    if (!formState.patientId) {
      setFormError("Selecione um paciente.")
      return
    }

    if (!formState.psychologistId) {
      setFormError("Selecione um psicólogo.")
      return
    }

    if (!formState.date) {
      setFormError("Informe a data da consulta.")
      return
    }

    if (!formState.time) {
      setFormError("Informe o horário da consulta.")
      return
    }

    if (!formState.type) {
      setFormError("Selecione o tipo de consulta.")
      return
    }

    const appointmentDate = buildLocalDateTime(formState.date, formState.time)
    const endTime = addMinutesToTime(formState.time, 60)
    const appointmentEndDate = buildLocalDateTime(formState.date, endTime)

    const trimmedNotes = formState.notes.trim()

    const basePayload = {
      appointmentDate,
      appointmentEndDate,
      psychologistId: Number(formState.psychologistId),
      patientIds: [Number(formState.patientId)],
      type: formState.type,
      notes: trimmedNotes.length > 0 ? trimmedNotes : null,
    }

    setIsSubmitting(true)
    try {
      if (dialogMode === "create") {
        const createPayload: CreateAppointmentPayload = {
          ...basePayload,
          paymentId: null,
        }
        await appointmentController.createAppointment(createPayload)
      } else if (editingAppointment) {
        const updatePayload: UpdateAppointmentPayload = {
          ...basePayload,
          paymentId: editingAppointment.paymentId ? Number(editingAppointment.paymentId) : null,
        }
        await appointmentController.updateAppointment(editingAppointment.id, updatePayload)
      }

      // CORREÇÃO: Chamando a função correta
      await loadAppointments(true) // Força a busca de novos dados
      closeAppointmentDialog()
    } catch (err) {
      console.error("Erro ao salvar agendamento", err)
      setFormError(err instanceof Error ? err.message : "Erro ao salvar agendamento")
    } finally {
      setIsSubmitting(false)
    }
  }

  const todayAppointments = useMemo(
    () => appointments.filter((appointment) => appointment.date === currentDate.toISOString().split("T")[0]),
    [appointments, currentDate],
  )

  const handleDeleteAppointment = async () => {
    if (!appointmentToDelete) return

    setIsDeleting(true)
    try {
      await appointmentController.deleteAppointment(appointmentToDelete.id)
      // CORREÇÃO: Chamando a função correta
      await loadAppointments(true) // Força a busca de novos dados
      setAppointmentToDelete(null)
      setIsDeleteDialogOpen(false)
    } catch (err) {
      console.error("Erro ao excluir agendamento", err)
      setError(err instanceof Error ? err.message : "Erro ao excluir agendamento")
    } finally {
      setIsDeleting(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Agendamentos</h1>
          <p className="text-gray-600 dark:text-gray-400">Gerencie suas consultas</p>
        </div>

        <Button className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600" onClick={openCreateDialog}>
          <Plus className="mr-2 h-4 w-4" />
          Nova Consulta
        </Button>
        <Dialog open={isAppointmentDialogOpen} onOpenChange={(open) => (!open ? closeAppointmentDialog() : undefined)}>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>{dialogMode === "create" ? "Agendar Nova Consulta" : "Editar Consulta"}</DialogTitle>
              <DialogDescription>
                {dialogMode === "create"
                  ? "Preencha os dados da consulta"
                  : "Atualize as informações da consulta selecionada"}
              </DialogDescription>
            </DialogHeader>
            <div className="grid grid-cols-2 gap-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="patient">Paciente</Label>
                <Select
                  value={formState.patientId}
                  onValueChange={(value) => setFormState((prev) => ({ ...prev, patientId: value }))}
                >
                  <SelectTrigger>
                    <SelectValue
                      placeholder={patients.length ? "Selecione o paciente" : "Nenhum paciente cadastrado"}
                    />
                  </SelectTrigger>
                  <SelectContent>
                    {patients.length === 0 ? (
                      <SelectItem value="no-patients" disabled>
                        Nenhum paciente disponível
                      </SelectItem>
                    ) : (
                      patients.map((patient) => (
                        <SelectItem key={patient.id} value={patient.id}>
                          {patient.name}
                        </SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="psychologist">Psicólogo</Label>
                <Select
                  value={formState.psychologistId}
                  onValueChange={(value) => setFormState((prev) => ({ ...prev, psychologistId: value }))}
                >
                  <SelectTrigger>
                    <SelectValue
                      placeholder={psychologists.length ? "Selecione o psicólogo" : "Nenhum psicólogo cadastrado"}
                    />
                  </SelectTrigger>
                  <SelectContent>
                    {psychologists.length === 0 ? (
                      <SelectItem value="no-psychologists" disabled>
                        Nenhum psicólogo disponível
                      </SelectItem>
                    ) : (
                      psychologists.map((psychologist) => (
                        <SelectItem key={psychologist.id} value={psychologist.id}>
                          {psychologist.fullName}
                        </SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="date">Data</Label>
                <Input
                  id="date"
                  type="date"
                  value={formState.date}
                  onChange={(event) =>
                    setFormState((prev) => ({
                      ...prev,
                      date: event.target.value,
                    }))
                  }
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="time">Horário</Label>
                <Select
                  value={formState.time}
                  onValueChange={(value) => setFormState((prev) => ({ ...prev, time: value }))}
                >
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
                <Select
                  value={formState.type}
                  onValueChange={(value) => setFormState((prev) => ({ ...prev, type: value }))}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione o tipo" />
                  </SelectTrigger>
                  <SelectContent>
                    {appointmentTypes.map((option) => (
                      <SelectItem key={option.value} value={option.value}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="col-span-2 space-y-2">
                <Label htmlFor="notes">Observações</Label>
                <Textarea
                  id="notes"
                  placeholder="Observações sobre a consulta..."
                  value={formState.notes}
                  onChange={(event) =>
                    setFormState((prev) => ({
                      ...prev,
                      notes: event.target.value,
                    }))
                  }
                />
              </div>
            </div>
            {formError && <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>}
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={closeAppointmentDialog} disabled={isSubmitting}>
                Cancelar
              </Button>
              <Button
                className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
                onClick={handleSubmitAppointment}
                disabled={isSubmitting}
              >
                {dialogMode === "create"
                  ? isSubmitting
                    ? "Agendando..."
                    : "Agendar"
                  : isSubmitting
                    ? "Salvando..."
                    : "Salvar"}
              </Button>
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

      {/* Interactive Calendar */}
      <InteractiveCalendar
        appointments={appointments}
        currentDate={currentDate}
        onDateSelect={(date) => {
          setSelectedDate(date)
          setCurrentDate(new Date(date + "T12:00:00")); // Adiciona T12:00:00 para evitar problemas de fuso
        }}
        onNewAppointment={openCreateDialog}
      />

      {/* Appointments List */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CalendarIcon className="h-5 w-5" />
            Consultas do Dia
          </CardTitle>
          <CardDescription>
            {error
              ? `Erro: ${error}`
              : isLoading
                ? "Carregando consultas..."
                : `${todayAppointments.length} consulta(s) agendada(s)`}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {error && !isLoading && (
              <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
            )}
            {!isLoading && todayAppointments.length === 0 && !error ? (
              <p className="text-sm text-gray-500 dark:text-gray-400">Nenhuma consulta para o dia selecionado.</p>
            ) : null}
            {todayAppointments.map((appointment) => (
              <div
                key={appointment.id}
                className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
              >
                <div className="flex items-center gap-4">
                  <div className="text-center">
                    <div className="text-lg font-semibold text-blue-600 dark:text-blue-400">
                      {appointment.time ?? "--:--"}
                    </div>
                  </div>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <User className="h-4 w-4 text-gray-400" />
                      <p className="font-medium text-gray-900 dark:text-gray-100">
                        {appointment.primaryPatientName ?? "Paciente"}
                      </p>
                    </div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{appointment.type}</p>
                    {appointment.notes && (
                      <p className="text-xs text-gray-500 dark:text-gray-500">{appointment.notes}</p>
                    )}
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  {getStatusBadge(appointment.status)}
                  <div className="flex gap-1">
                    <Button variant="ghost" size="sm" onClick={() => openEditDialog(appointment)}>
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => {
                        setAppointmentToDelete(appointment)
                        setIsDeleteDialogOpen(true)
                      }}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      <AlertDialog
        open={isDeleteDialogOpen}
        onOpenChange={(open) => {
          if (!open) {
            setIsDeleteDialogOpen(false)
            setAppointmentToDelete(null)
          } else {
            setIsDeleteDialogOpen(true)
          }
        }}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover o agendamento
              {appointmentToDelete ? (
                <>
                  {" "}
                  de <strong>{appointmentToDelete.primaryPatientName ?? "Paciente"}</strong> em {" "}
                  <strong>{new Date(`${appointmentToDelete.date}T12:00:00`).toLocaleDateString("pt-BR")}</strong>
                  {appointmentToDelete.time ? ` às ${appointmentToDelete.time}` : ""}?
                </>
              ) : (
                " selecionado?"
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isDeleting}>Voltar</AlertDialogCancel>
            <AlertDialogAction onClick={handleDeleteAppointment} disabled={isDeleting} className="bg-red-600 hover:bg-red-700">
              {isDeleting ? "Excluindo..." : "Excluir"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
