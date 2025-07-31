package com.example.ampliar.dto.appointment;

import java.time.LocalDateTime;

public record AppointmentDTO(
        Long id,
        LocalDateTime appointmentDate,
        Long psychologistId,
        Long patientId,
        Long paymentId
){}
