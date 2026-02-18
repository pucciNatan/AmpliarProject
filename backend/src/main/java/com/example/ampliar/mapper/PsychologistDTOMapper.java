package com.example.ampliar.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.example.ampliar.dto.psychologist.PsychologistDTO;
import com.example.ampliar.model.PsychologistModel;

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
