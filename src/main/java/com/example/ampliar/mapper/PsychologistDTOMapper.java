package com.example.ampliar.mapper;

import com.example.ampliar.dto.psychologist.PsychologistDTO;
import com.example.ampliar.model.PsychologistModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PsychologistDTOMapper implements Function<PsychologistModel, PsychologistDTO> {

    @Override
    public PsychologistDTO apply(PsychologistModel psychologist) {
        return new PsychologistDTO(
                psychologist.getId(),
                psychologist.getFullName(),
                psychologist.getCpf(),
                psychologist.getPhoneNumber(),
                psychologist.getEmail()
        );
    }
}