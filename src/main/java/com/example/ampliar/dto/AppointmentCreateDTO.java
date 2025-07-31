package com.example.ampliar.dto;

import java.time.LocalDateTime;
import com.example.ampliar.validation.constraints.AppointmentDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentCreateDTO(
        @AppointmentDate
        LocalDateTime appointmentDate,

        @NotNull(message = "O psicólogo não pode ser nulo.")
        Long psychologistId,
        @NotNull(message = "O paciente não pode ser nulo.")
        Long patientId,
        
        Long paymentId
){}
