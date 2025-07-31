package com.example.ampliar.mapper;

import com.example.ampliar.dto.patient.PatientDTO;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.function.Function;

@Service
public class PatientDTOMapper implements Function<PatientModel, PatientDTO> {

    @Override
    public PatientDTO apply(PatientModel patientModel) {
        List<Long> guardianIds = patientModel.getLegalGuardians()
                .stream()
                .map(LegalGuardianModel::getId)
                .toList();

        return new PatientDTO(
                patientModel.getId(),
                patientModel.getFullName(),
                patientModel.getCpf(),
                patientModel.getPhoneNumber(),
                patientModel.getBirthDate(),
                guardianIds);
    }
}
