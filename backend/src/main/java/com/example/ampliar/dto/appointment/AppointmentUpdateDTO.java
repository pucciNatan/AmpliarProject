// AppointmentUpdateDTO.java
package com.example.ampliar.dto.appointment;

import java.time.LocalDateTime;
import java.util.List;

import com.example.ampliar.model.enums.AppointmentStatus;
import com.example.ampliar.validation.constraints.AppointmentDate;

public record AppointmentUpdateDTO(
        @AppointmentDate
        LocalDateTime appointmentDate,
        LocalDateTime appointmentEndDate,
        AppointmentStatus status,
        String type,
        String notes,
        Long psychologistId,
        List<Long> patientIds,
        Long paymentId
) {}
