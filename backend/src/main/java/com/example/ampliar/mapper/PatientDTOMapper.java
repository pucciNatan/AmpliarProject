package com.example.ampliar.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.example.ampliar.dto.patient.PatientDTO;
import com.example.ampliar.model.AppointmentModel;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;

@Service
public class PatientDTOMapper implements Function<PatientModel, PatientDTO> {

    @Override
    public PatientDTO apply(PatientModel patientModel) {
        List<Long> guardianIds = patientModel.getLegalGuardians()
                .stream()
                .map(LegalGuardianModel::getId)
                .toList();

        List<AppointmentModel> appointments = patientModel.getAppointments();

        int totalAppointments = appointments != null ? appointments.size() : 0;

        LocalDateTime lastAppointmentDate = appointments == null || appointments.isEmpty()
                ? null
                : appointments.stream()
                        .map(AppointmentModel::getAppointmentDate)
                        .filter(Objects::nonNull)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);

        String status = totalAppointments > 0 ? "active" : "inactive";

        return new PatientDTO(
                patientModel.getId(),
                patientModel.getFullName(),
                patientModel.getCpf(),
                patientModel.getPhoneNumber(),
                patientModel.getBirthDate(),
                guardianIds,
                status,
                totalAppointments,
                lastAppointmentDate
        );
    }
}
