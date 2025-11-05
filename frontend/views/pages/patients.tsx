"use client"

import { useState, useEffect, useMemo, useCallback } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Badge } from "@/components/ui/badge"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Search, Plus, Edit, Eye, Phone, Mail, CalendarIcon, Trash2, Users } from "lucide-react"
import type { Patient } from "@/models/patient"
import { PatientController, PatientCreatePayload, PatientUpdatePayload } from "@/controllers/patient-controller"
import { Alert, AlertDescription } from "@/components/ui/alert"
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
// NOVAS IMPORTAÇÕES
import { LegalGuardianController } from "@/controllers/legal-guardian-controller"
import type { LegalGuardian, CreateLegalGuardianPayload, UpdateLegalGuardianPayload } from "@/models/legal-guardian"

// --- Tipos de Estado ---
type DialogMode = "createPatient" | "editPatient" | "createGuardian" | "editGuardian"
type DeletionTarget = { id: string; name: string; type: "patient" | "guardian" }

export function Patients() {
  // --- Estados de Dados ---
  const [patients, setPatients] = useState<Patient[]>([])
  const [guardians, setGuardians] = useState<LegalGuardian[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // --- Estados da UI ---
  const [searchTerm, setSearchTerm] = useState("")
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [dialogMode, setDialogMode] = useState<DialogMode>("createPatient")
  const [editingItem, setEditingItem] = useState<Patient | LegalGuardian | null>(null)
  const [isDeleting, setIsDeleting] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [deletionTarget, setDeletionTarget] = useState<DeletionTarget | null>(null)
  const [formError, setFormError] = useState<string | null>(null)

  // --- Estados de Formulários ---
  const [patientFormState, setPatientFormState] = useState<PatientCreatePayload>({
    name: "",
    cpf: "",
    phone: "",
    birthDate: "",
    email: "",
    address: "",
    notes: "",
    legalGuardianIds: [],
  })
  const [guardianFormState, setGuardianFormState] = useState<Omit<CreateLegalGuardianPayload, "patientIds">>({
    fullName: "",
    cpf: "",
    phoneNumber: "",
  })

  // --- Controllers ---
  const patientController = useMemo(() => PatientController.getInstance(), [])
  const guardianController = useMemo(() => LegalGuardianController.getInstance(), [])

  // --- Mapeamento de Dados ---
  const guardianMap = useMemo(() => new Map(guardians.map((g) => [g.id, g.fullName])), [guardians])

  const patientsWithMetadata = useMemo(
    () =>
      patients.map((patient) => ({
        ...patient,
        guardianName: patient.legalGuardianIds.length > 0
          ? guardianMap.get(patient.legalGuardianIds[0]) ?? "Inválido"
          : "N/D",
      })),
    [patients, guardianMap],
  )

  const filteredPatients = useMemo(
    () =>
      patientsWithMetadata.filter(
        (patient) =>
          patient.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          patient.cpf.includes(searchTerm) ||
          patient.phone.includes(searchTerm),
      ),
    [patientsWithMetadata, searchTerm],
  )

  // --- Carregamento de Dados ---
  const loadData = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const [patientsData, guardiansData] = await Promise.all([
        patientController.getPatients(),
        guardianController.getGuardians(),
      ])
      setPatients(patientsData)
      setGuardians(guardiansData)
    } catch (err: any) {
      setError(err.message || "Erro ao buscar dados.")
    } finally {
      setIsLoading(false)
    }
  }, [patientController, guardianController])

  useEffect(() => {
    void loadData()
  }, [loadData])

  // --- Funções de Abertura/Fechamento de Modais ---
  const resetForms = () => {
    setFormError(null)
    setIsSubmitting(false)
    setEditingItem(null)
    setPatientFormState({
      name: "",
      cpf: "",
      phone: "",
      birthDate: "",
      email: "",
      address: "",
      notes: "",
      legalGuardianIds: [],
    })
    setGuardianFormState({ fullName: "", cpf: "", phoneNumber: "" })
  }

  const openDialog = (mode: DialogMode, item: Patient | LegalGuardian | null = null) => {
    resetForms()
    setDialogMode(mode)
    setEditingItem(item)

    if (mode === "editPatient" && item) {
      const patient = item as Patient
      setPatientFormState({
        name: patient.name,
        cpf: patient.cpf,
        phone: patient.phone,
        birthDate: patient.birthDate.slice(0, 10),
        email: patient.email ?? "", // CORREÇÃO: Garantir que não seja null/undefined
        address: patient.address ?? "", // CORREÇÃO: Garantir que não seja null/undefined
        notes: patient.notes ?? "", // CORREÇÃO: Garantir que não seja null/undefined
        legalGuardianIds: patient.legalGuardianIds ?? [],
      })
    } else if (mode === "editGuardian" && item) {
      const guardian = item as LegalGuardian
      setGuardianFormState({
        fullName: guardian.fullName,
        cpf: guardian.cpf,
        phoneNumber: guardian.phoneNumber,
      })
    }
    setIsDialogOpen(true)
  }

  const openDeleteDialog = (target: DeletionTarget) => {
    setDeletionTarget(target)
    setIsDeleteDialogOpen(true)
  }

  // --- Handlers de Submit ---
  const handlePatientSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setFormError(null)
    setIsSubmitting(true)

    const payload = {
      ...patientFormState,
      legalGuardianIds: patientFormState.legalGuardianIds.filter(id => id), // Garante que não há IDs vazios
    }

    try {
      if (dialogMode === "createPatient") {
        await patientController.createPatient(payload)
      } else if (editingItem) {
        await patientController.updatePatient(editingItem.id, payload)
      }

      await loadData()
      setIsDialogOpen(false)
    } catch (err: any) {
      setFormError(err.message || "Erro ao salvar paciente.")
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleGuardianSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setFormError(null)
    setIsSubmitting(true)

    // NOTA: O backend (LegalGuardianCreateDTO) exige `patientIds`.
    // Estamos enviando uma lista vazia, assumindo que o backend será ajustado.
    const payload = {
      ...guardianFormState,
      patientIds: (editingItem as LegalGuardian)?.patientIds?.map(String) ?? [],
    }

    try {
      if (dialogMode === "createGuardian") {
        await guardianController.createGuardian(payload)
      } else if (editingItem) {
        await guardianController.updateGuardian(editingItem.id, payload)
      }

      await loadData()
      setIsDialogOpen(false)
    } catch (err: any) {
      setFormError(err.message || "Erro ao salvar responsável.")
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDelete = async () => {
    if (!deletionTarget) return

    setIsDeleting(true)
    setError(null)
    try {
      if (deletionTarget.type === "patient") {
        await patientController.deletePatient(deletionTarget.id)
      } else if (deletionTarget.type === "guardian") {
        await guardianController.deleteGuardian(deletionTarget.id)
      }
      await loadData()
      setIsDeleteDialogOpen(false)
      setDeletionTarget(null)
    } catch (err: any) {
      setError(err.message || "Erro ao excluir.")
    } finally {
      setIsDeleting(false)
    }
  }

  // --- Funções Utilitárias ---
  const formatCPF = (cpf: string) => cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
  const formatPhone = (phone: string) => phone.replace(/(\d{2})(\d{4,5})(\d{4})/, "($1) $2-$3")
  const getStatusBadge = (status: "active" | "inactive") =>
    status === "active" ? (
      <Badge className="bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200">Ativo</Badge>
    ) : (
      <Badge className="bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300">Inativo</Badge>
    )

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Pacientes</h1>
          <p className="text-gray-600 dark:text-gray-400">Gerencie seus pacientes e responsáveis</p>
        </div>
        <Button
          className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
          onClick={() => openDialog("createPatient")}
        >
          <Plus className="mr-2 h-4 w-4" />
          Novo Paciente
        </Button>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <Card>
        <CardContent className="pt-6">
          <div className="relative">
            <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
            <Input
              placeholder="Buscar por nome, CPF ou telefone..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>
        </CardContent>
      </Card>

      {/* Tabela de Pacientes */}
      <Card>
        <CardHeader>
          <CardTitle>Lista de Pacientes</CardTitle>
          <CardDescription>
            {isLoading ? "Carregando..." : `${filteredPatients.length} paciente(s) encontrado(s)`}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Nome</TableHead>
                <TableHead>Contato</TableHead>
                <TableHead>Responsável</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Consultas</TableHead>
                <TableHead>Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center">Carregando...</TableCell>
                </TableRow>
              ) : filteredPatients.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center">Nenhum paciente cadastrado.</TableCell>
                </TableRow>
              ) : (
                filteredPatients.map((patient) => (
                  <TableRow key={patient.id}>
                    <TableCell className="font-medium">{patient.name}</TableCell>
                    <TableCell>
                      <div className="text-sm">{formatPhone(patient.phone)}</div>
                      <div className="text-xs text-gray-500">{patient.email}</div>
                    </TableCell>
                    <TableCell>{patient.guardianName}</TableCell>
                    <TableCell>{getStatusBadge(patient.status)}</TableCell>
                    <TableCell>{patient.totalAppointments}</TableCell>
                    <TableCell>
                      <div className="flex gap-2">
                        <Button variant="ghost" size="sm" onClick={() => openDialog("editPatient", patient)}>
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => openDeleteDialog({ id: patient.id, name: patient.name, type: "patient" })}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Tabela de Responsáveis */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Users className="h-5 w-5" />
              Gerenciar Responsáveis Legais
            </CardTitle>
            <CardDescription>Pessoas responsáveis por pacientes.</CardDescription>
          </div>
          <Button variant="outline" size="sm" onClick={() => openDialog("createGuardian")}>
            <Plus className="mr-2 h-4 w-4" />
            Novo Responsável
          </Button>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Nome</TableHead>
                <TableHead>CPF</TableHead>
                <TableHead>Telefone</TableHead>
                <TableHead>Pacientes Vinculados</TableHead>
                <TableHead className="text-right">Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={5} className="text-center">Carregando...</TableCell>
                </TableRow>
              ) : guardians.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} className="text-center">Nenhum responsável cadastrado.</TableCell>
                </TableRow>
              ) : (
                guardians.map((guardian) => (
                  <TableRow key={guardian.id}>
                    <TableCell className="font-medium">{guardian.fullName}</TableCell>
                    <TableCell>{formatCPF(guardian.cpf)}</TableCell>
                    <TableCell>{formatPhone(guardian.phoneNumber)}</TableCell>
                    <TableCell>{guardian.patientIds.length}</TableCell>
                    <TableCell className="text-right">
                      <Button variant="ghost" size="sm" onClick={() => openDialog("editGuardian", guardian)}>
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => openDeleteDialog({ id: guardian.id, name: guardian.fullName, type: "guardian" })}
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

      {/* Modal de Paciente e Responsável */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="max-w-2xl">
          <form onSubmit={dialogMode.includes("Patient") ? handlePatientSubmit : handleGuardianSubmit}>
            <DialogHeader>
              <DialogTitle>
                {dialogMode === "createPatient" && "Cadastrar Novo Paciente"}
                {dialogMode === "editPatient" && "Editar Paciente"}
                {dialogMode === "createGuardian" && "Cadastrar Novo Responsável"}
                {dialogMode === "editGuardian" && "Editar Responsável"}
              </DialogTitle>
            </DialogHeader>

            {formError && (
              <Alert variant="destructive" className="my-4">
                <AlertDescription>{formError}</AlertDescription>
              </Alert>
            )}

            {/* Formulário de Paciente */}
            {dialogMode.includes("Patient") && (
              <div className="grid grid-cols-2 gap-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Nome Completo</Label>
                  <Input id="name" value={patientFormState.name} onChange={(e) => setPatientFormState(prev => ({ ...prev, name: e.target.value }))} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="cpf">CPF</Label>
                  <Input id="cpf" value={patientFormState.cpf} onChange={(e) => setPatientFormState(prev => ({ ...prev, cpf: e.target.value }))} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="phone">Telefone</Label>
                  <Input id="phone" value={patientFormState.phone} onChange={(e) => setPatientFormState(prev => ({ ...prev, phone: e.target.value }))} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="birthDate">Data de Nascimento</Label>
                  <Input id="birthDate" type="date" value={patientFormState.birthDate} onChange={(e) => setPatientFormState(prev => ({ ...prev, birthDate: e.target.value }))} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  {/* CORREÇÃO: Garantir que o valor nunca seja null/undefined */}
                  <Input id="email" type="email" value={patientFormState.email ?? ""} onChange={(e) => setPatientFormState(prev => ({ ...prev, email: e.target.value }))} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="guardian">Responsável Legal</Label>
                  <Select
                    // CORREÇÃO: O valor do Select não pode ser string vazia. Usamos "none"
                    value={patientFormState.legalGuardianIds[0] ?? "none"}
                    onValueChange={(value) => setPatientFormState(prev => ({ ...prev, legalGuardianIds: value === "none" ? [] : [value] }))}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Nenhum (paciente responsável)" />
                    </SelectTrigger>
                    <SelectContent>
                      {/* CORREÇÃO: O valor "none" não pode ser uma string vazia */}
                      <SelectItem value="none">Nenhum (paciente responsável)</SelectItem>
                      {guardians.map(g => (
                        <SelectItem key={g.id} value={g.id}>{g.fullName}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="col-span-2 space-y-2">
                  <Label htmlFor="address">Endereço</Label>
                  {/* CORREÇÃO: Garantir que o valor nunca seja null/undefined */}
                  <Input id="address" value={patientFormState.address ?? ""} onChange={(e) => setPatientFormState(prev => ({ ...prev, address: e.target.value }))} />
                </div>
                <div className="col-span-2 space-y-2">
                  <Label htmlFor="notes">Observações</Label>
                  {/* CORREÇÃO: Garantir que o valor nunca seja null/undefined */}
                  <Input id="notes" value={patientFormState.notes ?? ""} onChange={(e) => setPatientFormState(prev => ({ ...prev, notes: e.target.value }))} />
                </div>
              </div>
            )}

            {/* Formulário de Responsável */}
            {dialogMode.includes("Guardian") && (
              <div className="grid grid-cols-2 gap-4 py-4">
                <div className="col-span-2 space-y-2">
                  <Label htmlFor="g-name">Nome Completo</Label>
                  <Input id="g-name" value={guardianFormState.fullName} onChange={(e) => setGuardianFormState(prev => ({ ...prev, fullName: e.target.value }))} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="g-cpf">CPF</Label>
                  <Input id="g-cpf" value={guardianFormState.cpf} onChange={(e) => setGuardianFormState(prev => ({ ...prev, cpf: e.target.value }))} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="g-phone">Telefone</Label>
                  <Input id="g-phone" value={guardianFormState.phoneNumber} onChange={(e) => setGuardianFormState(prev => ({ ...prev, phoneNumber: e.target.value }))} required />
                </div>
              </div>
            )}

            <div className="flex justify-end gap-2">
              <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)} disabled={isSubmitting}>
                Cancelar
              </Button>
              <Button type="submit" className="bg-blue-600 hover:bg-blue-700" disabled={isSubmitting}>
                {isSubmitting ? "Salvando..." : "Salvar"}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* Modal de Exclusão */}
      <AlertDialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover <strong>{deletionTarget?.name}</strong>?
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isDeleting}>Cancelar</AlertDialogCancel>
            <AlertDialogAction className="bg-red-600 hover:bg-red-700" disabled={isDeleting} onClick={handleDelete}>
              {isDeleting ? "Excluindo..." : "Excluir"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
