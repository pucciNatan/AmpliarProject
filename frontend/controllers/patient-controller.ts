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
}

const mapDtoToFrontend = (dto: PatientDTO): Patient => {
    return {
        id: dto.id.toString(),
        name: dto.fullName,
        cpf: dto.cpf,
        phone: dto.phoneNumber,
        birthDate: dto.birthDate,
        status: "active",
        totalAppointments: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    };
};

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
                ...data,
                legalGuardianIds: [], // Por enquanto, criamos sem responsáveis
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
}
