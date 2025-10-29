package com.example.ampliar.repository;

import com.example.ampliar.model.AppointmentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long>{
    boolean existsByAppointmentDateAndPsychologistId(LocalDateTime date, Long psychologistId);
    boolean existsByAppointmentDateAndPatientId(LocalDateTime date, Long patientId);
}

