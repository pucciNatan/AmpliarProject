"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { DollarSign, Plus, TrendingUp, TrendingDown, Calendar, CreditCard, AlertCircle } from "lucide-react"

interface Payment {
  id: number
  patientName: string
  amount: number
  method: "cash" | "card" | "pix" | "transfer"
  date: string
  status: "paid" | "pending" | "overdue"
  appointmentType: string
}

export function Finance() {
  const [isNewPaymentOpen, setIsNewPaymentOpen] = useState(false)
  const [filterStatus, setFilterStatus] = useState<"all" | "paid" | "pending" | "overdue">("all")

  const payments: Payment[] = [
    {
      id: 1,
      patientName: "Ana Carolina Santos",
      amount: 150.0,
      method: "pix",
      date: "2024-01-20",
      status: "paid",
      appointmentType: "Consulta Individual",
    },
    {
      id: 2,
      patientName: "João Pedro Silva",
      amount: 150.0,
      method: "card",
      date: "2024-01-19",
      status: "paid",
      appointmentType: "Terapia Cognitiva",
    },
    {
      id: 3,
      patientName: "Maria Fernanda Costa",
      amount: 200.0,
      method: "cash",
      date: "2024-01-15",
      status: "pending",
      appointmentType: "Avaliação Inicial",
    },
    {
      id: 4,
      patientName: "Carlos Eduardo Lima",
      amount: 150.0,
      method: "transfer",
      date: "2024-01-10",
      status: "overdue",
      appointmentType: "Consulta Individual",
    },
  ]

  const stats = [
    {
      title: "Receita Total",
      value: "R$ 12.450,00",
      change: "+12%",
      changeType: "positive" as const,
      icon: DollarSign,
    },
    {
      title: "Pagamentos Pendentes",
      value: "R$ 2.340,00",
      change: "12 pacientes",
      changeType: "negative" as const,
      icon: AlertCircle,
    },
    {
      title: "Recebido Este Mês",
      value: "R$ 8.750,00",
      change: "+8%",
      changeType: "positive" as const,
      icon: TrendingUp,
    },
    {
      title: "Média por Consulta",
      value: "R$ 165,00",
      change: "+5%",
      changeType: "positive" as const,
      icon: Calendar,
    },
  ]

  const filteredPayments =
    filterStatus === "all" ? payments : payments.filter((payment) => payment.status === filterStatus)

  const getStatusBadge = (status: Payment["status"]) => {
    const statusConfig = {
      paid: { label: "Pago", className: "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200" },
      pending: {
        label: "Pendente",
        className: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200",
      },
      overdue: { label: "Vencido", className: "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200" },
    }

    const config = statusConfig[status]
    return <Badge className={config.className}>{config.label}</Badge>
  }

  const getMethodLabel = (method: Payment["method"]) => {
    const methods = {
      cash: "Dinheiro",
      card: "Cartão",
      pix: "PIX",
      transfer: "Transferência",
    }
    return methods[method]
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(amount)
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Financeiro</h1>
          <p className="text-gray-600 dark:text-gray-400">Controle de pagamentos e receitas</p>
        </div>

        <Dialog open={isNewPaymentOpen} onOpenChange={setIsNewPaymentOpen}>
          <DialogTrigger asChild>
            <Button className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600">
              <Plus className="mr-2 h-4 w-4" />
              Registrar Pagamento
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-md">
            <DialogHeader>
              <DialogTitle>Registrar Pagamento</DialogTitle>
              <DialogDescription>Registre um novo pagamento recebido</DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4">
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
                <Label htmlFor="amount">Valor</Label>
                <Input id="amount" type="number" placeholder="0,00" step="0.01" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="method">Forma de Pagamento</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Selecione a forma" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="cash">Dinheiro</SelectItem>
                    <SelectItem value="card">Cartão</SelectItem>
                    <SelectItem value="pix">PIX</SelectItem>
                    <SelectItem value="transfer">Transferência</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="date">Data do Pagamento</Label>
                <Input id="date" type="date" />
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setIsNewPaymentOpen(false)}>
                Cancelar
              </Button>
              <Button className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600">
                Registrar
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon
          return (
            <Card key={stat.title}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">{stat.title}</CardTitle>
                <Icon className="h-4 w-4 text-gray-400" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{stat.value}</div>
                <p
                  className={`text-xs flex items-center gap-1 ${
                    stat.changeType === "positive"
                      ? "text-green-600 dark:text-green-400"
                      : "text-red-600 dark:text-red-400"
                  }`}
                >
                  {stat.changeType === "positive" ? (
                    <TrendingUp className="h-3 w-3" />
                  ) : (
                    <TrendingDown className="h-3 w-3" />
                  )}
                  {stat.change}
                </p>
              </CardContent>
            </Card>
          )
        })}
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex items-center gap-4">
            <Label>Filtrar por status:</Label>
            <Select value={filterStatus} onValueChange={(value: any) => setFilterStatus(value)}>
              <SelectTrigger className="w-48">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Todos</SelectItem>
                <SelectItem value="paid">Pagos</SelectItem>
                <SelectItem value="pending">Pendentes</SelectItem>
                <SelectItem value="overdue">Vencidos</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Payments Table */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5" />
            Histórico de Pagamentos
          </CardTitle>
          <CardDescription>{filteredPayments.length} pagamento(s) encontrado(s)</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Paciente</TableHead>
                <TableHead>Valor</TableHead>
                <TableHead>Forma de Pagamento</TableHead>
                <TableHead>Data</TableHead>
                <TableHead>Tipo de Consulta</TableHead>
                <TableHead>Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredPayments.map((payment) => (
                <TableRow key={payment.id}>
                  <TableCell className="font-medium">{payment.patientName}</TableCell>
                  <TableCell className="font-semibold text-green-600 dark:text-green-400">
                    {formatCurrency(payment.amount)}
                  </TableCell>
                  <TableCell>{getMethodLabel(payment.method)}</TableCell>
                  <TableCell>{new Date(payment.date).toLocaleDateString("pt-BR")}</TableCell>
                  <TableCell>{payment.appointmentType}</TableCell>
                  <TableCell>{getStatusBadge(payment.status)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  )
}
