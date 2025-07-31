package com.example.ampliar.dto;

import java.time.LocalDateTime;
import com.example.ampliar.validation.constraints.AppointmentDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AppointmentUpdateDTO(
        @AppointmentDate
        LocalDateTime appointmentDate,
        Long psychologistId,
        Long patientId,
        Long paymentId
){}
