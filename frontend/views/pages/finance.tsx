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
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { AlertCircle, CreditCard, DollarSign, Plus, TrendingUp, Users, Edit, Trash2 } from "lucide-react"
import { FinanceController } from "@/controllers/finance-controller"
import type { Payment, CreatePaymentPayload, UpdatePaymentPayload } from "@/models/payment"
import type { Payer, CreatePayerPayload, UpdatePayerPayload } from "@/models/payer"
// CORREÇÃO: Adicionado AlertDescription
import { Alert, AlertDescription } from "@/components/ui/alert"

const formatCurrency = (amount: number) =>
  new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(amount)

const formatDateForInput = (date: Date) => date.toISOString().split("T")[0]

const formatDateDisplay = (value: string) =>
  new Date(`${value}T00:00:00`).toLocaleDateString("pt-BR", {
    year: "numeric",
    month: "short",
    day: "2-digit",
  })

type DialogMode = "createPayment" | "editPayment" | "createPayer" | "editPayer"
type DeletionTarget = { id: string; name: string; type: "payment" | "payer" }

export function Finance() {
  const financeController = useMemo(() => FinanceController.getInstance(), [])

  // --- Estados de Dados ---
  const [payments, setPayments] = useState<Payment[]>([])
  const [payers, setPayers] = useState<Payer[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // --- Estados da UI ---
  const [dialogMode, setDialogMode] = useState<DialogMode>("createPayment")
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [deletionTarget, setDeletionTarget] = useState<DeletionTarget | null>(null)
  const [editingItem, setEditingItem] = useState<Payment | Payer | null>(null)
  const [formError, setFormError] = useState<string | null>(null)
  const [payerFilter, setPayerFilter] = useState<string>("all")

  // --- Estados de Formulário ---
  const [paymentFormState, setPaymentFormState] = useState<CreatePaymentPayload>({
    payerId: "",
    amount: 0,
    paymentDate: formatDateForInput(new Date()),
  })
  const [payerFormState, setPayerFormState] = useState<CreatePayerPayload>({
    fullName: "",
    cpf: "",
    phoneNumber: "",
  })

  // --- Carregamento de Dados ---
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

  // --- Memos de Dados Derivados ---
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
    const total = paymentsWithMetadata.reduce((sum, payment) => sum + payment.amount, 0)
    const count = paymentsWithMetadata.length
    const average = count > 0 ? total / count : 0
    const lastPaymentDate =
      count > 0 ? paymentsWithMetadata.map((p) => p.paymentDate).sort((a, b) => (a > b ? -1 : 1))[0] : null
    return { total, average, count, lastPaymentDate }
  }, [paymentsWithMetadata])

  // --- Funções de Reset e Abertura de Modais ---
  const resetForms = () => {
    setFormError(null)
    setIsSubmitting(false)
    setEditingItem(null)
    setPaymentFormState({
      payerId: "",
      amount: 0,
      paymentDate: formatDateForInput(new Date()),
    })
    setPayerFormState({ fullName: "", cpf: "", phoneNumber: "" })
  }

  const openDialog = (mode: DialogMode, item: Payment | Payer | null = null) => {
    resetForms()
    setDialogMode(mode)
    setEditingItem(item)

    if (mode === "editPayment" && item) {
      const payment = item as Payment
      setPaymentFormState({
        payerId: payment.payerId,
        amount: payment.amount,
        paymentDate: payment.paymentDate,
      })
    } else if (mode === "editPayer" && item) {
      const payer = item as Payer
      setPayerFormState({
        fullName: payer.fullName,
        cpf: payer.cpf,
        phoneNumber: payer.phoneNumber,
      })
    }
    setIsDialogOpen(true)
  }

  const openDeleteDialog = (target: DeletionTarget) => {
    setDeletionTarget(target)
    setIsDeleteDialogOpen(true)
  }

  // --- Handlers de Ações (CRUD) ---

  const handlePaymentSubmit = async () => {
    setFormError(null)
    if (!paymentFormState.payerId) return setFormError("Selecione o pagador.")
    if (!paymentFormState.amount || paymentFormState.amount <= 0) return setFormError("Informe um valor válido.")
    if (!paymentFormState.paymentDate) return setFormError("Informe a data.")

    setIsSubmitting(true)
    try {
      if (dialogMode === "createPayment") {
        await financeController.createPayment(paymentFormState)
      } else if (dialogMode === "editPayment" && editingItem) {
        await financeController.updatePayment(editingItem.id, paymentFormState)
      }
      await loadData()
      setIsDialogOpen(false)
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "Erro ao salvar pagamento.")
    } finally {
      setIsSubmitting(false)
    }
  }

  const handlePayerSubmit = async () => {
    setFormError(null)
    if (!payerFormState.fullName) return setFormError("Nome é obrigatório.")
    if (!payerFormState.cpf) return setFormError("CPF é obrigatório.")
    if (!payerFormState.phoneNumber) return setFormError("Telefone é obrigatório.")

    setIsSubmitting(true)
    try {
      if (dialogMode === "createPayer") {
        await financeController.createPayer(payerFormState)
      } else if (dialogMode === "editPayer" && editingItem) {
        await financeController.updatePayer(editingItem.id, payerFormState)
      }
      await loadData()
      setIsDialogOpen(false)
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "Erro ao salvar pagador.")
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDelete = async () => {
    if (!deletionTarget) return

    setIsDeleting(true)
    setError(null)
    try {
      if (deletionTarget.type === "payment") {
        await financeController.deletePayment(deletionTarget.id)
      } else if (deletionTarget.type === "payer") {
        await financeController.deletePayer(deletionTarget.id)
      }
      await loadData()
      setIsDeleteDialogOpen(false)
      setDeletionTarget(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erro ao excluir.")
    } finally {
      setIsDeleting(false)
    }
  }

  const hasPayers = payers.length > 0
  const isLoadingData = isLoading && !error

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Financeiro</h1>
          <p className="text-gray-600 dark:text-gray-400">Acompanhe os pagamentos registrados na clínica</p>
        </div>
        <Button
          className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
          onClick={() => openDialog("createPayment")}
          disabled={!hasPayers && !isLoadingData}
        >
          <Plus className="mr-2 h-4 w-4" />
          Registrar Pagamento
        </Button>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">Receita total</CardTitle>
            <DollarSign className="h-4 w-4 text-gray-400" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{formatCurrency(stats.total)}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">Pagamentos</CardTitle>
            <TrendingUp className="h-4 w-4 text-gray-400" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{stats.count}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">Ticket médio</CardTitle>
            <CreditCard className="h-4 w-4 text-gray-400" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{formatCurrency(stats.average)}</div>
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
          </CardContent>
        </Card>
      </div>

      {/* --- NOVA SEÇÃO: Gerenciamento de Pagadores --- */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Users className="h-5 w-5" />
              Gerenciar Pagadores
            </CardTitle>
            <CardDescription>Pessoas responsáveis pelos pagamentos.</CardDescription>
          </div>
          <Button variant="outline" size="sm" onClick={() => openDialog("createPayer")}>
            <Plus className="mr-2 h-4 w-4" />
            Novo Pagador
          </Button>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Nome</TableHead>
                <TableHead>CPF</TableHead>
                <TableHead>Telefone</TableHead>
                <TableHead className="text-right">Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoadingData ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center">Carregando...</TableCell>
                </TableRow>
              ) : payers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center">Nenhum pagador cadastrado.</TableCell>
                </TableRow>
              ) : (
                payers.map((payer) => (
                  <TableRow key={payer.id}>
                    <TableCell className="font-medium">{payer.fullName}</TableCell>
                    <TableCell>{payer.cpf}</TableCell>
                    <TableCell>{payer.phoneNumber}</TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="sm" onClick={() => openDialog("editPayer", payer)}>
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => openDeleteDialog({ id: payer.id, name: payer.fullName, type: "payer" })}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* --- Seção de Histórico de Pagamentos (Atualizada) --- */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5" />
            Histórico de Pagamentos
          </CardTitle>
          <CardDescription>
            {isLoadingData ? "Carregando..." : `${filteredPayments.length} registro(s) encontrado(s)`}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-end mb-4">
            <Select value={payerFilter} onValueChange={setPayerFilter} disabled={!hasPayers}>
              <SelectTrigger className="w-full md:w-64">
                <SelectValue placeholder="Filtrar por pagador" />
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

          {!isLoadingData && filteredPayments.length === 0 ? (
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
                  <TableHead className="text-right">Ações</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {isLoadingData ? (
                  <TableRow>
                    <TableCell colSpan={4} className="text-center">Carregando...</TableCell>
                  </TableRow>
                ) : (
                  filteredPayments.map((payment) => (
                    <TableRow key={payment.id}>
                      <TableCell className="font-medium">{payment.payerName}</TableCell>
                      <TableCell className="font-semibold text-green-600 dark:text-green-400">
                        {formatCurrency(payment.amount)}
                      </TableCell>
                      <TableCell>{formatDateDisplay(payment.paymentDate)}</TableCell>
                      <TableCell className="text-right">
                        <Button variant="ghost" size="sm" onClick={() => openDialog("editPayment", payment)}>
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() =>
                            openDeleteDialog({
                              id: payment.id,
                              name: `pagamento de ${formatCurrency(payment.amount)}`,
                              type: "payment",
                            })
                          }
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* --- Diálogo Unificado para Pagamentos e Pagadores --- */}
      <Dialog
        open={isDialogOpen}
        onOpenChange={(open) => {
          if (!open) setIsDialogOpen(false)
        }}
      >
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {dialogMode === "createPayment" && "Registrar Pagamento"}
              {dialogMode === "editPayment" && "Editar Pagamento"}
              {dialogMode === "createPayer" && "Novo Pagador"}
              {dialogMode === "editPayer" && "Editar Pagador"}
            </DialogTitle>
            <DialogDescription>
              {dialogMode.includes("Payment")
                ? "Informe os detalhes do pagamento."
                : "Informe os dados do pagador."}
            </DialogDescription>
          </DialogHeader>

          {formError && (
            <Alert variant="destructive" className="my-4">
              <AlertDescription>{formError}</AlertDescription>
            </Alert>
          )}

          {/* --- Formulário de Pagamento --- */}
          {dialogMode.includes("Payment") && (
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="payer">Pagador</Label>
                <Select
                  value={paymentFormState.payerId}
                  onValueChange={(value) => setPaymentFormState((prev) => ({ ...prev, payerId: value }))}
                  disabled={!hasPayers}
                >
                  <SelectTrigger>
                    <SelectValue placeholder={hasPayers ? "Selecione o pagador" : "Nenhum pagador"} />
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
                  value={paymentFormState.amount || ""}
                  onChange={(e) => setPaymentFormState((prev) => ({ ...prev, amount: Number(e.target.value) }))}
                  placeholder="0,00"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="paymentDate">Data do Pagamento</Label>
                <Input
                  id="paymentDate"
                  type="date"
                  value={paymentFormState.paymentDate}
                  onChange={(e) => setPaymentFormState((prev) => ({ ...prev, paymentDate: e.target.value }))}
                />
              </div>
            </div>
          )}

          {/* --- Formulário de Pagador --- */}
          {dialogMode.includes("Payer") && (
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="fullName">Nome Completo</Label>
                <Input
                  id="fullName"
                  value={payerFormState.fullName}
                  onChange={(e) => setPayerFormState((prev) => ({ ...prev, fullName: e.target.value }))}
                  placeholder="Nome do pagador"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="cpf">CPF</Label>
                <Input
                  id="cpf"
                  value={payerFormState.cpf}
                  onChange={(e) => setPayerFormState((prev) => ({ ...prev, cpf: e.target.value }))}
                  placeholder="000.000.000-00"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="phoneNumber">Telefone</Label>
                <Input
                  id="phoneNumber"
                  value={payerFormState.phoneNumber}
                  onChange={(e) => setPayerFormState((prev) => ({ ...prev, phoneNumber: e.target.value }))}
                  placeholder="(00) 00000-0000"
                />
              </div>
            </div>
          )}

          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setIsDialogOpen(false)} disabled={isSubmitting}>
              Cancelar
            </Button>
            <Button
              className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
              onClick={dialogMode.includes("Payment") ? handlePaymentSubmit : handlePayerSubmit}
              disabled={isSubmitting || (!hasPayers && dialogMode.includes("Payment"))}
            >
              {isSubmitting ? "Salvando..." : "Salvar"}
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* --- Diálogo de Exclusão --- */}
      <AlertDialog
        open={isDeleteDialogOpen}
        onOpenChange={(open) => {
          if (!open) setDeletionTarget(null)
          setIsDeleteDialogOpen(open)
        }}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja excluir
              <strong> {deletionTarget?.name}</strong>?
              {deletionTarget?.type === "payer" &&
                " (Isso também excluirá todos os pagamentos associados a ele.)"}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isDeleting}>Voltar</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete} disabled={isDeleting} className="bg-red-600 hover:bg-red-700">
              {isDeleting ? "Excluindo..." : "Excluir"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
