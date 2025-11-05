"use client"

import { useMemo, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { ChevronLeft, ChevronRight, Plus } from "lucide-react"
import { cn } from "@/lib/utils"
import type { Appointment } from "@/models/appointment"

const weekDays = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"]
const monthNames = [
  "Janeiro",
  "Fevereiro",
  "Março",
  "Abril",
  "Maio",
  "Junho",
  "Julho",
  "Agosto",
  "Setembro",
  "Outubro",
  "Novembro",
  "Dezembro",
]

const statusConfig: Record<Appointment["status"], { label: string; className: string }> = {
  scheduled: { label: "Agendado", className: "bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200" },
  completed: { label: "Concluído", className: "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200" },
  cancelled: { label: "Cancelado", className: "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200" },
  "no-show": {
    label: "Faltou",
    className: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200",
  },
}

const getStatusBadge = (status: Appointment["status"]) => {
  const config = statusConfig[status]
  return <Badge className={`${config.className} text-xs`}>{config.label}</Badge>
}

interface InteractiveCalendarProps {
  currentDate: Date
  appointments: Appointment[]
  onDateSelect: (date: string) => void
  onNewAppointment: () => void
}

export function InteractiveCalendar({ appointments, currentDate, onDateSelect, onNewAppointment }: InteractiveCalendarProps) {
  const [selectedDate, setSelectedDate] = useState<string | null>(null)
  const [showDayModal, setShowDayModal] = useState(false)
  const [modalAppointments, setModalAppointments] = useState<Appointment[]>([])

  const calendarMonth = useMemo(() => {
    const year = currentDate.getFullYear()
    const month = currentDate.getMonth()
    const firstDay = new Date(year, month, 1)
    const startDate = new Date(firstDay)
    startDate.setDate(startDate.getDate() - firstDay.getDay())

    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const days = Array.from({ length: 42 }, (_, index) => {
      const current = new Date(startDate)
      current.setDate(startDate.getDate() + index)
      const dateString = current.toISOString().split("T")[0]
      const dayAppointments = appointments.filter((appointment) => appointment.date === dateString)

      return {
        date: new Date(current),
        appointments: dayAppointments,
        isCurrentMonth: current.getMonth() === month,
        isToday: current.getTime() === today.getTime(),
        isSelected: selectedDate === dateString,
      }
    })

    return {
      year,
      month,
      days,
    }
  }, [appointments, currentDate, selectedDate])

  const handleDateClick = (date: Date) => {
    const dateString = date.toISOString().split("T")[0]
    const dayAppointments = appointments.filter((appointment) => appointment.date === dateString)

    setSelectedDate(dateString)
    setModalAppointments(dayAppointments)
    setShowDayModal(true)
    onDateSelect(dateString)
  }

  const navigateMonth = (direction: "prev" | "next") => {
    const newDate = new Date(currentDate)
    newDate.setMonth(newDate.getMonth() + (direction === "next" ? 1 : -1))
    onDateSelect(newDate.toISOString().split("T")[0])
  }

  return (
    <>
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="text-lg font-semibold">
              {monthNames[calendarMonth.month]} {calendarMonth.year}
            </CardTitle>
            <div className="flex items-center gap-2">
              <Button variant="outline" size="sm" onClick={() => navigateMonth("prev")}>
                <ChevronLeft className="h-4 w-4" />
              </Button>
              <Button variant="outline" size="sm" onClick={() => navigateMonth("next")}>
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-7 gap-1 mb-4">
            {weekDays.map((day) => (
              <div key={day} className="p-2 text-center text-sm font-medium text-gray-500 dark:text-gray-400">
                {day}
              </div>
            ))}
          </div>

          <div className="grid grid-cols-7 gap-1">
            {calendarMonth.days.map((day, index) => {
              const hasAppointments = day.appointments.length > 0
              const isSelected = selectedDate === day.date.toISOString().split("T")[0]

              return (
                <button
                  key={index}
                  onClick={() => handleDateClick(day.date)}
                  className={cn(
                    "p-2 min-h-[60px] text-left border rounded-lg transition-colors hover:bg-gray-50 dark:hover:bg-gray-800",
                    day.isCurrentMonth
                      ? "border-gray-200 dark:border-gray-700"
                      : "border-gray-100 dark:border-gray-800 text-gray-400 dark:text-gray-600",
                    day.isToday && "bg-blue-50 dark:bg-blue-950 border-blue-200 dark:border-blue-800",
                    isSelected && "bg-blue-100 dark:bg-blue-900 border-blue-300 dark:border-blue-600",
                    hasAppointments && "border-green-200 dark:border-green-800",
                  )}
                >
                  <div className="flex flex-col h-full">
                    <span
                      className={cn(
                        "text-sm font-medium",
                        day.isToday && "text-blue-600 dark:text-blue-400",
                        !day.isCurrentMonth && "text-gray-400 dark:text-gray-600",
                      )}
                    >
                      {day.date.getDate()}
                    </span>

                    {hasAppointments && (
                      <div className="flex-1 mt-1">
                        <div className="flex flex-wrap gap-1">
                          {day.appointments.slice(0, 2).map((appointment) => (
                            <div
                              key={appointment.id}
                              className="w-2 h-2 rounded-full bg-blue-500 dark:bg-blue-400"
                              title={`${appointment.time ?? "--:--"} - ${appointment.primaryPatientName ?? "Paciente"}`}
                            />
                          ))}
                          {day.appointments.length > 2 && (
                            <span className="text-xs text-gray-500 dark:text-gray-400">
                              +{day.appointments.length - 2}
                            </span>
                          )}
                        </div>
                      </div>
                    )}
                  </div>
                </button>
              )
            })}
          </div>
        </CardContent>
      </Card>

      {/* Day Modal */}
      <Dialog open={showDayModal} onOpenChange={setShowDayModal}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>
              Consultas -{" "}
              {selectedDate &&
                new Date(selectedDate + "T00:00:00").toLocaleDateString("pt-BR", {
                  weekday: "long",
                  year: "numeric",
                  month: "long",
                  day: "numeric",
                })}
            </DialogTitle>
            <DialogDescription>{modalAppointments.length} consulta(s) neste dia</DialogDescription>
          </DialogHeader>

          <div className="space-y-4 max-h-96 overflow-y-auto">
            {modalAppointments.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-gray-500 dark:text-gray-400 mb-4">Nenhuma consulta agendada para este dia</p>
                <Button
                  onClick={() => {
                    setShowDayModal(false)
                    onNewAppointment()
                  }}
                  className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
                >
                  <Plus className="mr-2 h-4 w-4" />
                  Agendar Consulta
                </Button>
              </div>
            ) : (
              <>
                {modalAppointments.map((appointment) => (
                  <div
                    key={appointment.id}
                    className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg"
                  >
                    <div className="flex items-center gap-4">
                      <div className="text-center">
                        <div className="text-lg font-semibold text-blue-600 dark:text-blue-400">
                          {appointment.time ?? "--:--"}
                        </div>
                        <div className="text-xs text-gray-500 dark:text-gray-400">{appointment.endTime ?? "--:--"}</div>
                      </div>
                      <div>
                        <p className="font-medium text-gray-900 dark:text-gray-100">
                          {appointment.primaryPatientName ?? "Paciente"}
                        </p>
                        <p className="text-sm text-gray-600 dark:text-gray-400">{appointment.type}</p>
                        {appointment.notes && (
                          <p className="text-xs text-gray-500 dark:text-gray-500 mt-1">{appointment.notes}</p>
                        )}
                      </div>
                    </div>
                    <div className="flex items-center gap-2">{getStatusBadge(appointment.status)}</div>
                  </div>
                ))}

                <div className="pt-4 border-t border-gray-200 dark:border-gray-700">
                  <Button
                    onClick={() => {
                      setShowDayModal(false)
                      onNewAppointment()
                    }}
                    className="w-full bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
                  >
                    <Plus className="mr-2 h-4 w-4" />
                    Agendar Nova Consulta
                  </Button>
                </div>
              </>
            )}
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}
