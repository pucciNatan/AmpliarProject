"use client"

import { useCallback, useEffect, useMemo, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
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
import { AlertCircle, CreditCard, DollarSign, Plus, TrendingUp } from "lucide-react"
import { FinanceController } from "@/controllers/finance-controller"
import type { Payment } from "@/models/payment"
import type { Payer } from "@/models/payer"

const formatCurrency = (amount: number) =>
  new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(amount)

const formatDateForInput = (date: Date) => date.toISOString().split("T")[0]

const formatDateDisplay = (value: string) =>
  new Date(`${value}T00:00:00`).toLocaleDateString("pt-BR", {
    year: "numeric",
    month: "short",
    day: "2-digit",
  })

export function Finance() {
  const financeController = useMemo(() => FinanceController.getInstance(), [])

  const [payments, setPayments] = useState<Payment[]>([])
  const [payers, setPayers] = useState<Payer[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  const [formState, setFormState] = useState({
    payerId: "",
    amount: "",
    paymentDate: formatDateForInput(new Date()),
  })

  const [payerFilter, setPayerFilter] = useState<string>("all")

  const loadData = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const [paymentsResponse, payersResponse] = await Promise.all([
        financeController.getPayments(),
        financeController.getPayers(),
      ])

      setPayments(paymentsResponse)
      setPayers(payersResponse)
    } catch (err) {
      const message = err instanceof Error ? err.message : "Não foi possível carregar os dados financeiros."
      setError(message)
    } finally {
      setIsLoading(false)
    }
  }, [financeController])

  useEffect(() => {
    void loadData()
  }, [loadData])

  const payerMap = useMemo(() => new Map(payers.map((payer) => [payer.id, payer])), [payers])

  const paymentsWithMetadata = useMemo(
    () =>
      payments.map((payment) => ({
        ...payment,
        payerName: payerMap.get(payment.payerId)?.fullName ?? "Pagador desconhecido",
      })),
    [payments, payerMap],
  )

  const filteredPayments = useMemo(() => {
    if (payerFilter === "all") {
      return paymentsWithMetadata
    }

    return paymentsWithMetadata.filter((payment) => payment.payerId === payerFilter)
  }, [paymentsWithMetadata, payerFilter])

  const stats = useMemo(() => {
    if (paymentsWithMetadata.length === 0) {
      return {
        total: 0,
        average: 0,
        count: 0,
        lastPaymentDate: null as string | null,
      }
    }

    const total = paymentsWithMetadata.reduce((sum, payment) => sum + payment.amount, 0)
    const count = paymentsWithMetadata.length
    const average = total / count

    const lastPaymentDate = paymentsWithMetadata
      .map((payment) => payment.paymentDate)
      .sort((a, b) => (a > b ? -1 : 1))[0]

    return {
      total,
      average,
      count,
      lastPaymentDate,
    }
  }, [paymentsWithMetadata])

  const resetForm = () => {
    setFormState({
      payerId: "",
      amount: "",
      paymentDate: formatDateForInput(new Date()),
    })
    setFormError(null)
    setIsSubmitting(false)
  }

  const handleCreatePayment = async () => {
    setFormError(null)

    if (!formState.payerId) {
      setFormError("Selecione o pagador responsável pelo pagamento.")
      return
    }

    if (!formState.amount || Number(formState.amount) <= 0) {
      setFormError("Informe um valor válido para o pagamento.")
      return
    }

    if (!formState.paymentDate) {
      setFormError("Informe a data do pagamento.")
      return
    }

    const payload = {
      payerId: formState.payerId,
      amount: Number(formState.amount),
      paymentDate: formState.paymentDate,
    }

    setIsSubmitting(true)
    try {
      await financeController.createPayment(payload)
      await loadData()
      setIsDialogOpen(false)
      resetForm()
    } catch (err) {
      const message = err instanceof Error ? err.message : "Erro ao registrar pagamento."
      setFormError(message)
    } finally {
      setIsSubmitting(false)
    }
  }

  const hasPayers = payers.length > 0
  const showEmptyState = !isLoading && filteredPayments.length === 0

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Financeiro</h1>
          <p className="text-gray-600 dark:text-gray-400">
            {error ? error : "Acompanhe os pagamentos registrados na clínica"}
          </p>
        </div>

        <Dialog
          open={isDialogOpen}
          onOpenChange={(open) => {
            setIsDialogOpen(open)
            if (!open) {
              resetForm()
            }
          }}
        >
          <DialogTrigger asChild>
            <Button className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600" disabled={!hasPayers}>
              <Plus className="mr-2 h-4 w-4" />
              Registrar Pagamento
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-md">
            <DialogHeader>
              <DialogTitle>Registrar Pagamento</DialogTitle>
              <DialogDescription>Informe os detalhes do pagamento recebido.</DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="payer">Pagador</Label>
                <Select
                  value={formState.payerId}
                  onValueChange={(value) => setFormState((prev) => ({ ...prev, payerId: value }))}
                  disabled={!hasPayers}
                >
                  <SelectTrigger>
                    <SelectValue
                      placeholder={hasPayers ? "Selecione o pagador" : "Cadastre um pagador para continuar"}
                    />
                  </SelectTrigger>
                  <SelectContent>
                    {payers.map((payer) => (
                      <SelectItem key={payer.id} value={payer.id}>
                        {payer.fullName}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="amount">Valor</Label>
                <Input
                  id="amount"
                  type="number"
                  min="0"
                  step="0.01"
                  value={formState.amount}
                  onChange={(event) => setFormState((prev) => ({ ...prev, amount: event.target.value }))}
                  placeholder="0,00"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="paymentDate">Data do Pagamento</Label>
                <Input
                  id="paymentDate"
                  type="date"
                  value={formState.paymentDate}
                  onChange={(event) => setFormState((prev) => ({ ...prev, paymentDate: event.target.value }))}
                />
              </div>
              {formError && <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>}
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                Cancelar
              </Button>
              <Button
                className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
                onClick={handleCreatePayment}
                disabled={isSubmitting || !hasPayers}
              >
                {isSubmitting ? "Registrando..." : "Registrar"}
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">Receita total</CardTitle>
            <DollarSign className="h-4 w-4 text-gray-400" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{formatCurrency(stats.total)}</div>
            <p className="text-xs text-gray-500 dark:text-gray-400">Valor acumulado de todos os pagamentos registrados.</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">Pagamentos registrados</CardTitle>
            <TrendingUp className="h-4 w-4 text-gray-400" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{stats.count}</div>
            <p className="text-xs text-gray-500 dark:text-gray-400">Quantidade total de registros financeiros.</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">Ticket médio</CardTitle>
            <CreditCard className="h-4 w-4 text-gray-400" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">
              {formatCurrency(stats.count > 0 ? stats.average : 0)}
            </div>
            <p className="text-xs text-gray-500 dark:text-gray-400">Valor médio recebido por pagamento.</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">Último pagamento</CardTitle>
            <AlertCircle className="h-4 w-4 text-gray-400" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">
              {stats.lastPaymentDate ? formatDateDisplay(stats.lastPaymentDate) : "--"}
            </div>
            <p className="text-xs text-gray-500 dark:text-gray-400">Data do registro financeiro mais recente.</p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
            <div>
              <p className="text-sm font-medium text-gray-700 dark:text-gray-300">Filtrar por pagador</p>
              <p className="text-xs text-gray-500 dark:text-gray-400">
                Visualize apenas os pagamentos de um responsável específico.
              </p>
            </div>
            <Select value={payerFilter} onValueChange={setPayerFilter} disabled={payers.length === 0}>
              <SelectTrigger className="w-full md:w-64">
                <SelectValue placeholder="Selecione o pagador" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Todos os pagadores</SelectItem>
                {payers.map((payer) => (
                  <SelectItem key={payer.id} value={payer.id}>
                    {payer.fullName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5" />
            Histórico de Pagamentos
          </CardTitle>
          <CardDescription>
            {isLoading ? "Carregando pagamentos..." : `${filteredPayments.length} registro(s) encontrado(s)`}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {showEmptyState ? (
            <div className="flex flex-col items-center justify-center py-12 text-center text-sm text-gray-500 dark:text-gray-400">
              <CreditCard className="mb-4 h-8 w-8 text-gray-400" />
              <p>Nenhum pagamento encontrado para o filtro selecionado.</p>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Pagador</TableHead>
                  <TableHead>Valor</TableHead>
                  <TableHead>Data</TableHead>
                  <TableHead>ID</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredPayments.map((payment) => (
                  <TableRow key={payment.id}>
                    <TableCell className="font-medium">{payment.payerName}</TableCell>
                    <TableCell className="font-semibold text-green-600 dark:text-green-400">
                      {formatCurrency(payment.amount)}
                    </TableCell>
                    <TableCell>{formatDateDisplay(payment.paymentDate)}</TableCell>
                    <TableCell className="text-xs text-gray-500 dark:text-gray-400">#{payment.id}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
