package com.example.ampliar.mapper;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.example.ampliar.dto.patient.PatientDTO;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.repository.AppointmentRepository;

@Service
public class PatientDTOMapper implements Function<PatientModel, PatientDTO> {

    private final AppointmentRepository appointmentRepository;

    public PatientDTOMapper(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public PatientDTO apply(PatientModel patientModel) {
        List<Long> guardianIds = patientModel.getLegalGuardians()
                .stream()
                .map(LegalGuardianModel::getId)
                .toList();

        Integer totalAppointments = appointmentRepository.countByPatientsContainsAndPsychologistId(
            patientModel,
            patientModel.getPsychologist().getId()
        );

        return new PatientDTO(
                patientModel.getId(),
                patientModel.getFullName(),
                patientModel.getPhoneNumber(),
                patientModel.getEmail(),
                patientModel.getCpf(),
                patientModel.getBirthDate(),
                patientModel.getAddress(),
                patientModel.getNotes(),
                patientModel.getFirstConsultationDate(),
                guardianIds,
                totalAppointments
        );
    }
}
