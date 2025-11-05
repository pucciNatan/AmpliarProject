import { api } from "@/lib/api-client";
import type { Patient } from "@/models/patient";

interface PatientCreateData {
    fullName: string;
    cpf: string;
    phoneNumber: string;
    birthDate: string;
}

interface PatientDTO {
    id: number;
    fullName: string;
    cpf: string;
    phoneNumber: string;
    birthDate: string;
    legalGuardianIds: string[];
    status?: string;
    totalAppointments?: number;
    lastAppointmentDate?: string | null;
}

const mapDtoToFrontend = (dto: PatientDTO): Patient => {
    return {
        id: dto.id.toString(),
        name: dto.fullName,
        cpf: dto.cpf,
        phone: dto.phoneNumber,
        birthDate: dto.birthDate,
        status: (dto.status as Patient["status"]) ?? "active",
        totalAppointments: dto.totalAppointments ?? 0,
        lastAppointment: dto.lastAppointmentDate ?? undefined,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    };
};

const sanitizeCpf = (cpf: string | undefined) => (cpf ? cpf.replace(/\D/g, "") : undefined);
const sanitizePhone = (phone: string | undefined) => (phone ? phone.replace(/\D/g, "") : undefined);

export class PatientController {
    private static instance: PatientController;

    static getInstance(): PatientController {
        if (!PatientController.instance) {
            PatientController.instance = new PatientController();
        }
        return PatientController.instance;
    }

    async getPatients(): Promise<Patient[]> {
        try {
            const patientDtos = (await api("/patients", { method: "GET" })) as PatientDTO[];
            return patientDtos.map(mapDtoToFrontend);
        } catch (error) {
            console.error("Erro ao buscar pacientes:", error);
            throw error;
        }
    }

    async createPatient(data: PatientCreateData): Promise<Patient> {
        try {
            const payload = {
                fullName: data.fullName,
                cpf: sanitizeCpf(data.cpf),
                phoneNumber: sanitizePhone(data.phoneNumber),
                legalGuardianIds: [], // Por enquanto, criamos sem responsáveis
                birthDate: data.birthDate,
            };

            const newPatientDto = (await api("/patients", {
                method: "POST",
                body: payload,
            })) as PatientDTO;

            return mapDtoToFrontend(newPatientDto);

        } catch (error) {
            console.error("Erro ao criar paciente:", error);
            throw error;
        }
    }

    async updatePatient(id: string, data: Partial<PatientCreateData>): Promise<Patient> {
        try {
            const payload: Record<string, unknown> = {
                ...data,
            };

            if (data.cpf !== undefined) {
                payload.cpf = sanitizeCpf(data.cpf);
            }

            if (data.phoneNumber !== undefined) {
                payload.phoneNumber = sanitizePhone(data.phoneNumber);
            }

            const updatedDto = (await api(`/patients/${id}`, {
                method: "PUT",
                body: payload,
            })) as PatientDTO;

            return mapDtoToFrontend(updatedDto);

        } catch (error) {
            console.error(`Erro ao atualizar paciente ${id}:`, error);
            throw error;
        }
    }

    async deletePatient(id: string): Promise<void> {
        try {
            await api(`/patients/${id}`, { method: "DELETE" });
        } catch (error) {
            console.error(`Erro ao deletar paciente ${id}:`, error);
            throw error;
        }
    }
}
