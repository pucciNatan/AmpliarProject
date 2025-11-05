"use client"

import { useState, useEffect } from "react"
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
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Search, Plus, Edit, Eye, Phone, Mail, CalendarIcon, Trash2 } from "lucide-react"
import type { Patient } from "@/models/patient"
import { PatientController } from "@/controllers/patient-controller"
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

export function Patients() {
  const [patients, setPatients] = useState<Patient[]>([])
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null)

  const [isPatientDialogOpen, setIsPatientDialogOpen] = useState(false)
  const [dialogMode, setDialogMode] = useState<"create" | "edit">("create")
  const [editingPatient, setEditingPatient] = useState<Patient | null>(null)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [patientToDelete, setPatientToDelete] = useState<Patient | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [formError, setFormError] = useState<string | null>(null)

  const [formState, setFormState] = useState({
    fullName: "",
    cpf: "",
    phoneNumber: "",
    birthDate: "",
  })

  const patientController = PatientController.getInstance()

  const fetchPatients = async () => {
    try {
      setIsLoading(true)
      setError(null)
      const fetchedPatients = await patientController.getPatients()
      setPatients(fetchedPatients)
    } catch (err: any) {
      setError(err.message || "Erro ao buscar pacientes.")
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    fetchPatients()
  }, [])

  const resetForm = () => {
    setFormState({ fullName: "", cpf: "", phoneNumber: "", birthDate: "" })
    setFormError(null)
    setFormError(null)
    setIsSubmitting(false)
    setEditingPatient(null)
  }

  const openCreateDialog = () => {
    setDialogMode("create")
    resetForm()
    setIsPatientDialogOpen(true)
  }

  const openEditDialog = (patient: Patient) => {
    setDialogMode("edit")
    setEditingPatient(patient)
    setFormState({
      fullName: patient.name,
      cpf: patient.cpf,
      phoneNumber: patient.phone,
      birthDate: patient.birthDate.slice(0, 10),
    })
    setFormError(null)
    setIsSubmitting(false)
    setIsPatientDialogOpen(true)
  }

  const handleInputChange = (field: keyof typeof formState, value: string) => {
    setFormState((prev) => ({ ...prev, [field]: value }))
  }

  const filteredPatients = patients.filter(
    (patient) =>
      patient.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      patient.cpf.includes(searchTerm) ||
      patient.phone.includes(searchTerm),
  )

  const formatCPF = (cpf: string) => {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
  }

  const formatPhone = (phone: string) => {
    return phone.replace(/(\d{2})(\d{4,5})(\d{4})/, "($1) $2-$3")
  }

  const getStatusBadge = (status: "active" | "inactive") => {
    return status === "active" ? (
      <Badge className="bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200">Ativo</Badge>
    ) : (
      <Badge className="bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300">Inativo</Badge>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Pacientes</h1>
          <p className="text-gray-600 dark:text-gray-400">Gerencie seus pacientes</p>
        </div>

        <Button className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600" onClick={openCreateDialog}>
          <Plus className="mr-2 h-4 w-4" />
          Novo Paciente
        </Button>
        <Dialog
          open={isPatientDialogOpen}
          onOpenChange={(open) => {
            if (open) {
              setIsPatientDialogOpen(true)
            } else {
              setIsPatientDialogOpen(false)
              resetForm()
            }
          }}
        >
          <DialogContent className="max-w-2xl">
            <form
              onSubmit={async (e) => {
                e.preventDefault()
                setFormError(null)
                setIsSubmitting(true)
                try {
                  if (dialogMode === "create") {
                    await patientController.createPatient(formState)
                  } else if (editingPatient) {
                    await patientController.updatePatient(editingPatient.id, formState)
                  }

                  await fetchPatients()
                  setIsPatientDialogOpen(false)
                  resetForm()
                } catch (err: any) {
                  setFormError(err.message || "Erro ao salvar paciente.")
                } finally {
                  setIsSubmitting(false)
                }
              }}
            >
              <DialogHeader>
                <DialogTitle>
                  {dialogMode === "create" ? "Cadastrar Novo Paciente" : "Editar Paciente"}
                </DialogTitle>
                <DialogDescription>
                  {dialogMode === "create"
                    ? "Preencha os dados do paciente"
                    : "Atualize as informações do paciente"}
                </DialogDescription>
              </DialogHeader>

              {formError && (
                <Alert variant="destructive" className="my-4">
                  <AlertDescription>{formError}</AlertDescription>
                </Alert>
              )}

              <div className="grid grid-cols-2 gap-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Nome Completo</Label>
                  <Input
                    id="name"
                    placeholder="Nome do paciente"
                    value={formState.fullName}
                    onChange={(e) => handleInputChange("fullName", e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="cpf">CPF</Label>
                  <Input
                    id="cpf"
                    placeholder="000.000.000-00"
                    value={formState.cpf}
                    onChange={(e) => handleInputChange("cpf", e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="phone">Telefone</Label>
                  <Input
                    id="phone"
                    placeholder="(00) 00000-0000"
                    value={formState.phoneNumber}
                    onChange={(e) => handleInputChange("phoneNumber", e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="birthDate">Data de Nascimento</Label>
                  <Input
                    id="birthDate"
                    type="date"
                    value={formState.birthDate}
                    onChange={(e) => handleInputChange("birthDate", e.target.value)}
                    required
                  />
                </div>
              </div>
              <div className="flex justify-end gap-2">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    setIsPatientDialogOpen(false)
                    resetForm()
                  }}
                  disabled={isSubmitting}
                >
                  Cancelar
                </Button>
                <Button
                  type="submit"
                  className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? "Salvando..." : dialogMode === "create" ? "Cadastrar" : "Salvar"}
                </Button>
              </div>
            </form>
          </DialogContent>
        </Dialog>
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

      <Card>
        <CardHeader>
          <CardTitle>Lista de Pacientes</CardTitle>
          <CardDescription>
            {isLoading ? "Carregando pacientes..." : `${filteredPatients.length} paciente(s) encontrado(s)`}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Nome</TableHead>
                <TableHead>CPF</TableHead>
                <TableHead>Telefone</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Última Consulta</TableHead>
                <TableHead>Total Consultas</TableHead>
                <TableHead>Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={7} className="text-center">Carregando...</TableCell>
                </TableRow>
              ) : filteredPatients.length === 0 ? (
                 <TableRow>
                  <TableCell colSpan={7} className="text-center">Nenhum paciente cadastrado.</TableCell>
                </TableRow>
              ) : (
                filteredPatients.map((patient) => (
                  <TableRow key={patient.id}>
                    <TableCell className="font-medium">{patient.name}</TableCell>
                    <TableCell>{formatCPF(patient.cpf)}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-1 text-sm">
                        <Phone className="h-3 w-3" />
                        {formatPhone(patient.phone)}
                      </div>
                    </TableCell>
                    <TableCell>{getStatusBadge(patient.status)}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-1 text-sm">
                        <CalendarIcon className="h-3 w-3" />
                        {patient.lastAppointment ? new Date(patient.lastAppointment).toLocaleDateString("pt-BR") : "N/A"}
                      </div>
                    </TableCell>
                    <TableCell>{patient.totalAppointments}</TableCell>
                    <TableCell>
                      <div className="flex gap-2">
                        <Button variant="ghost" size="sm" onClick={() => setSelectedPatient(patient)}>
                          <Eye className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="sm" onClick={() => openEditDialog(patient)}>
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => {
                            setPatientToDelete(patient)
                            setIsDeleteDialogOpen(true)
                          }}
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

      <Dialog open={!!selectedPatient} onOpenChange={() => setSelectedPatient(null)}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Detalhes do Paciente</DialogTitle>
            <DialogDescription>Informações completas do paciente</DialogDescription>
          </DialogHeader>
          {selectedPatient && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label className="text-sm font-medium text-gray-600 dark:text-gray-400">Nome</Label>
                  <p className="text-sm">{selectedPatient.name}</p>
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-600 dark:text-gray-400">CPF</Label>
                  <p className="text-sm">{formatCPF(selectedPatient.cpf)}</p>
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-600 dark:text-gray-400">Telefone</Label>
                  <p className="text-sm">{formatPhone(selectedPatient.phone)}</p>
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-600 dark:text-gray-400">Data de Nascimento</Label>
                  <p className="text-sm">{new Date(selectedPatient.birthDate).toLocaleDateString("pt-BR")}</p>
                </div>
                <div>
                  <Label className="text-sm font-medium text-gray-600 dark:text-gray-400">Status</Label>
                  <div className="mt-1">{getStatusBadge(selectedPatient.status)}</div>
                </div>
              </div>

              <div className="pt-4 border-t border-gray-200 dark:border-gray-700">
                <h4 className="font-medium mb-2">Histórico de Consultas</h4>
                <div className="space-y-2 text-sm text-gray-600 dark:text-gray-400">
                  <p>Total de consultas: {selectedPatient.totalAppointments}</p>
                  <p>
                    Última consulta:{" "}
                    {selectedPatient.lastAppointment
                      ? new Date(selectedPatient.lastAppointment).toLocaleDateString("pt-BR")
                      : "N/A"}
                  </p>
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      <AlertDialog
        open={isDeleteDialogOpen}
        onOpenChange={(open) => {
          if (!open) {
            setIsDeleteDialogOpen(false)
            setPatientToDelete(null)
          } else {
            setIsDeleteDialogOpen(true)
          }
        }}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover o paciente
              {patientToDelete ? (
                <>
                  {" "}
                  <strong>{patientToDelete.name}</strong>?
                </>
              ) : (
                " selecionado?"
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isDeleting}>Cancelar</AlertDialogCancel>
            <AlertDialogAction
              className="bg-red-600 hover:bg-red-700"
              disabled={isDeleting}
              onClick={async () => {
                if (!patientToDelete) return
                setIsDeleting(true)
                try {
                  await patientController.deletePatient(patientToDelete.id)
                  await fetchPatients()
                  setPatientToDelete(null)
                  setIsDeleteDialogOpen(false)
                } catch (err: any) {
                  setError(err.message || "Erro ao excluir paciente.")
                } finally {
                  setIsDeleting(false)
                }
              }}
            >
              {isDeleting ? "Excluindo..." : "Excluir"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
