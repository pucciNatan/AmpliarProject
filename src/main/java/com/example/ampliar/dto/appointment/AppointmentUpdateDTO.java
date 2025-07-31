package com.example.ampliar.dto.appointment;

import java.time.LocalDateTime;
import com.example.ampliar.validation.constraints.AppointmentDate;

public record AppointmentUpdateDTO(
        @AppointmentDate
        LocalDateTime appointmentDate,
        Long psychologistId,
        Long patientId,
        Long paymentId
){}
