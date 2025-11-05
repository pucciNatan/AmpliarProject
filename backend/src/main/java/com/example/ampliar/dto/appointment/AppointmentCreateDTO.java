package com.example.ampliar.dto.appointment;

import java.time.LocalDateTime;
import java.util.List;

import com.example.ampliar.model.enums.AppointmentStatus;
import com.example.ampliar.validation.constraints.AppointmentDate;
import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentCreateDTO(
        @AppointmentDate
        LocalDateTime appointmentDate,

        LocalDateTime appointmentEndDate,

        AppointmentStatus status,

        @NotBlank(message = "O tipo da consulta é obrigatório")
        String type,

        String notes,

        @NotNull(message = "O psicólogo é obrigatório.")
        Long psychologistId,

        @NotNull(message = "Informe pelo menos um paciente.")
        @JsonAlias({"patientId"})
        List<Long> patientIds,

        Long paymentId
) {}
