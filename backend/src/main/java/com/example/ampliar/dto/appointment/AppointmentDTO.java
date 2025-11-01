package com.example.ampliar.dto.appointment;

import java.time.LocalDateTime;
import java.util.List;

public record AppointmentDTO(
        Long id,
        LocalDateTime appointmentDate,
        Long psychologistId,
        List<Long> patientIds,
        Long paymentId
) {}