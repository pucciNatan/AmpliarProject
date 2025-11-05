"use client"

import { useEffect, useState } from "react"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  DollarSign,
  Users,
  Calendar,
  Clock,
  AlertCircle,
} from "lucide-react"
import {
  DashboardData,
  dashboardController,
} from "@/controllers/dashboard-controller"
import { Skeleton } from "@/components/ui/skeleton"
// CORREÇÃO: Importando o tipo 'Page' do layout
import type { Page } from "../layout/dashboard-layout"

// CORREÇÃO: Definindo as props que o componente recebe
interface DashboardProps {
  onPageChange?: (page: Page) => void
}

export function Dashboard({ onPageChange }: DashboardProps) {
  const [data, setData] = useState<DashboardData | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true)
        const dashboardData = await dashboardController.getDashboardData()
        setData(dashboardData)
      } catch (error) {
        console.error("Failed to load dashboard data:", error)
      } finally {
        setIsLoading(false)
      }
    }
    fetchData()
  }, [])

  const formatCurrency = (value: number = 0) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(value)
  }

  const getStatusBadge = (status: "confirmed" | "pending" | "paid" | "overdue" | "scheduled") => {
     switch (status) {
      case "scheduled":
        return <Badge className="bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200">Agendado</Badge>
      case "paid":
        return <Badge className="bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200">Pago</Badge>
      case "pending":
         return <Badge className="bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200">Pendente</Badge>
       case "overdue":
         return <Badge className="bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200">Vencido</Badge>
      default:
        return <Badge variant="secondary">{status}</Badge>
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
          Dashboard
        </h1>
        <p className="text-gray-600 dark:text-gray-400">
          Visão geral do seu consultório
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {isLoading ? (
          <>
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
          </>
        ) : (
          <>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">
                  Receita Mensal
                </CardTitle>
                <DollarSign className="h-4 w-4 text-gray-400" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {formatCurrency(data?.stats.monthlyRevenue)}
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">
                  Pacientes Ativos
                </CardTitle>
                <Users className="h-4 w-4 text-gray-400" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {data?.stats.activePatients}
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">
                  Consultas Hoje
                </CardTitle>
                <Calendar className="h-4 w-4 text-gray-400" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {data?.stats.todayAppointmentsCount}
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">
                  Pagamentos Pendentes
                </CardTitle>
                <AlertCircle className="h-4 w-4 text-gray-400" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                  {formatCurrency(data?.stats.pendingRevenue)}
                </div>
              </CardContent>
            </Card>
          </>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="h-5 w-5" />
              Próximas Consultas
            </CardTitle>
            <CardDescription>
              {isLoading ? "Carregando..." : `${data?.upcomingAppointments.length || 0} próximas consultas agendadas`}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <Skeleton className="h-48" />
            ) : (
              <div className="space-y-4">
                {data?.upcomingAppointments.length === 0 ? (
                  <p className="text-sm text-gray-500 text-center py-4">Nenhuma próxima consulta agendada.</p>
                ) : (
                  data?.upcomingAppointments.map((appointment) => (
                    <div
                      key={appointment.id}
                      className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg"
                    >
                      <div className="flex items-center gap-4">
                        <div className="text-center">
                          <div className="text-lg font-semibold text-blue-600 dark:text-blue-400">
                            {appointment.time}
                          </div>
                          <div className="text-xs text-gray-500 dark:text-gray-400">
                            {new Date(appointment.date + "T12:00:00").toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })}
                          </div>
                        </div>
                        <div>
                          <p className="font-medium text-gray-900 dark:text-gray-100">
                            {appointment.primaryPatientName}
                          </p>
                          <p className="text-sm text-gray-600 dark:text-gray-400">
                            {appointment.type}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {getStatusBadge(appointment.paymentStatus)}
                      </div>
                    </div>
                  ))
                )}
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Ações Rápidas</CardTitle>
            <CardDescription>
              Acesso rápido às principais funcionalidades
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            {/* CORREÇÃO: Botões de Ação Rápida conectados */}
            <Button
              className="w-full justify-start bg-transparent"
              variant="outline"
              onClick={() => onPageChange?.("patients")}
            >
              <Users className="mr-2 h-4 w-4" />
              Novo Paciente
            </Button>
            <Button
              className="w-full justify-start bg-transparent"
              variant="outline"
              onClick={() => onPageChange?.("appointments")}
            >
              <Calendar className="mr-2 h-4 w-4" />
              Agendar Consulta
            </Button>
            <Button
              className="w-full justify-start bg-transparent"
              variant="outline"
              onClick={() => onPageChange?.("finance")}
            >
              <DollarSign className="mr-2 h-4 w-4" />
              Registrar Pagamento
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
