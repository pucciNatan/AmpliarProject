package com.example.ampliar.dto.appointment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.ampliar.model.enums.AppointmentStatus;

public record AppointmentDTO(
        Long id,
        LocalDateTime appointmentDate,
        LocalDateTime appointmentEndDate,
        AppointmentStatus status,
        String type,
        String notes,
        PsychologistSummary psychologist,
        List<PatientSummary> patients,
        String paymentStatus,
        BigDecimal paymentAmount,
        Long paymentId
) {
    public record PsychologistSummary(Long id, String fullName) {}
    public record PatientSummary(Long id, String fullName) {}
}
