"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { DollarSign, Users, Calendar, TrendingUp, Clock, AlertCircle } from "lucide-react"

export function Dashboard() {
  const stats = [
    {
      title: "Receita Mensal",
      value: "R$ 12.450,00",
      change: "+12%",
      changeType: "positive" as const,
      icon: DollarSign,
    },
    {
      title: "Pacientes Ativos",
      value: "89",
      change: "+3",
      changeType: "positive" as const,
      icon: Users,
    },
    {
      title: "Consultas Hoje",
      value: "8",
      change: "2 pendentes",
      changeType: "neutral" as const,
      icon: Calendar,
    },
    {
      title: "Pagamentos Pendentes",
      value: "R$ 2.340,00",
      change: "12 pacientes",
      changeType: "negative" as const,
      icon: AlertCircle,
    },
  ]

  const upcomingAppointments = [
    {
      id: 1,
      patient: "Ana Carolina Santos",
      time: "09:00",
      type: "Consulta Individual",
      status: "confirmed" as const,
    },
    {
      id: 2,
      patient: "João Pedro Silva",
      time: "10:30",
      type: "Terapia Cognitiva",
      status: "confirmed" as const,
    },
    {
      id: 3,
      patient: "Maria Fernanda Costa",
      time: "14:00",
      type: "Avaliação Inicial",
      status: "pending" as const,
    },
    {
      id: 4,
      patient: "Carlos Eduardo Lima",
      time: "15:30",
      type: "Consulta Individual",
      status: "confirmed" as const,
    },
    {
      id: 5,
      patient: "Beatriz Almeida",
      time: "17:00",
      type: "Terapia Familiar",
      status: "confirmed" as const,
    },
  ]

  const getStatusBadge = (status: "confirmed" | "pending") => {
    switch (status) {
      case "confirmed":
        return (
          <Badge variant="default" className="bg-green-100 text-green-800">
            Confirmado
          </Badge>
        )
      case "pending":
        return (
          <Badge variant="secondary" className="bg-yellow-100 text-yellow-800">
            Pendente
          </Badge>
        )
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600">Visão geral do seu consultório</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon
          return (
            <Card key={stat.title}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-gray-600">{stat.title}</CardTitle>
                <Icon className="h-4 w-4 text-gray-400" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-900">{stat.value}</div>
                <p
                  className={`text-xs flex items-center gap-1 ${
                    stat.changeType === "positive"
                      ? "text-green-600"
                      : stat.changeType === "negative"
                        ? "text-red-600"
                        : "text-gray-600"
                  }`}
                >
                  {stat.changeType === "positive" && <TrendingUp className="h-3 w-3" />}
                  {stat.change}
                </p>
              </CardContent>
            </Card>
          )
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Upcoming Appointments */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="h-5 w-5" />
              Próximas Consultas
            </CardTitle>
            <CardDescription>Agendamentos para hoje</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {upcomingAppointments.map((appointment) => (
                <div
                  key={appointment.id}
                  className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <div className="flex items-center gap-4">
                    <div className="text-center">
                      <div className="text-lg font-semibold text-blue-600">{appointment.time}</div>
                    </div>
                    <div>
                      <p className="font-medium text-gray-900">{appointment.patient}</p>
                      <p className="text-sm text-gray-600">{appointment.type}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">{getStatusBadge(appointment.status)}</div>
                </div>
              ))}
            </div>
            <div className="mt-4 pt-4 border-t">
              <Button variant="outline" className="w-full bg-transparent">
                Ver Todos os Agendamentos
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Quick Actions */}
        <Card>
          <CardHeader>
            <CardTitle>Ações Rápidas</CardTitle>
            <CardDescription>Acesso rápido às principais funcionalidades</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            <Button className="w-full justify-start bg-transparent" variant="outline">
              <Users className="mr-2 h-4 w-4" />
              Novo Paciente
            </Button>
            <Button className="w-full justify-start bg-transparent" variant="outline">
              <Calendar className="mr-2 h-4 w-4" />
              Agendar Consulta
            </Button>
            <Button className="w-full justify-start bg-transparent" variant="outline">
              <DollarSign className="mr-2 h-4 w-4" />
              Registrar Pagamento
            </Button>
            <Button className="w-full justify-start bg-transparent" variant="outline">
              <TrendingUp className="mr-2 h-4 w-4" />
              Relatórios
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
