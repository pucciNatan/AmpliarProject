package com.example.ampliar.dto;

import java.time.LocalDateTime;

public record AppointmentDTO(
        Long id,
        LocalDateTime appointmentDate,
        Long psychologistId,
        Long patientId,
        Long paymentId
){}
