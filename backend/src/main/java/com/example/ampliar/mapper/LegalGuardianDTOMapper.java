package com.example.ampliar.mapper;

import com.example.ampliar.dto.legalGuardian.LegalGuardianDTO;
import com.example.ampliar.model.LegalGuardianModel;
import com.example.ampliar.model.PatientModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LegalGuardianDTOMapper implements Function<LegalGuardianModel, LegalGuardianDTO> {

    @Override
    public LegalGuardianDTO apply(LegalGuardianModel guardian) {
        List<Long> patientIds = guardian.getPatients()
                .stream()
                .map(PatientModel::getId)
                .collect(Collectors.toList());

        return new LegalGuardianDTO(
                guardian.getId(),
                guardian.getFullName(),
                guardian.getCpf(),
                guardian.getPhoneNumber(),
                patientIds
        );
    }
}
