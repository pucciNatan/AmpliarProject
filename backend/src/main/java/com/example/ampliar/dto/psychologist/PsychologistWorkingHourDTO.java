package com.example.ampliar.dto.psychologist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PsychologistWorkingHourDTO(
        String dayOfWeek,
        String startTime,
        String endTime,
        Boolean enabled
) {}
