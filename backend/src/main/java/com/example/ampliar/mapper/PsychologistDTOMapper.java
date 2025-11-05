package com.example.ampliar.mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.example.ampliar.dto.psychologist.PsychologistDTO;
import com.example.ampliar.dto.psychologist.PsychologistWorkingHourDTO;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.model.PsychologistWorkingHour;

@Service
public class PsychologistDTOMapper implements Function<PsychologistModel, PsychologistDTO> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public PsychologistDTO apply(PsychologistModel psychologist) {
        List<String> specialties = psychologist.getSpecialties() == null
                ? List.of()
                : List.copyOf(psychologist.getSpecialties());

        List<PsychologistWorkingHourDTO> workingHours = psychologist.getWorkingHours() == null
                ? List.of()
                : psychologist.getWorkingHours().stream()
                        .map(this::mapWorkingHour)
                        .toList();

        return new PsychologistDTO(
                psychologist.getId(),
                psychologist.getFullName(),
                psychologist.getCpf(),
                psychologist.getPhoneNumber(),
                psychologist.getEmail(),
                psychologist.getCrp(),
                psychologist.getAddress(),
                psychologist.getBio(),
                specialties,
                workingHours
        );
    }

    private PsychologistWorkingHourDTO mapWorkingHour(PsychologistWorkingHour hour) {
        return new PsychologistWorkingHourDTO(
                hour.getDayOfWeek(),
                formatTime(hour.getStartTime()),
                formatTime(hour.getEndTime()),
                hour.isEnabled()
        );
    }

    private String formatTime(java.time.LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(TIME_FORMATTER);
    }
}
