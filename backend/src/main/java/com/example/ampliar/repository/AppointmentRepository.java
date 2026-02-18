package com.example.ampliar.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ampliar.model.AppointmentModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.model.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long> {

    List<AppointmentModel> findByPatientsContainingAndStatus(PatientModel patient, AppointmentStatus status);

    boolean existsByAppointmentDateAndPsychologistIdAndStatusIn(LocalDateTime appointmentDate, Long psychologistId, List<AppointmentStatus> status);

    boolean existsByAppointmentDateAndPatients_IdAndStatusIn(LocalDateTime appointmentDate, Long patientId, List<AppointmentStatus> status);

    boolean existsByAppointmentDateAndPsychologistIdAndStatusInAndIdNot(LocalDateTime appointmentDate, Long psychologistId, List<AppointmentStatus> status, Long id);

    boolean existsByAppointmentDateAndPatients_IdAndStatusInAndIdNot(LocalDateTime appointmentDate, Long patientId, List<AppointmentStatus> status, Long id);

    Integer countByPatientsContains(PatientModel patient);

    Integer countByPatientsContainsAndPsychologistId(PatientModel patient, Long psychologistId);

    List<AppointmentModel> findByPsychologistId(Long psychologistId);
    Optional<AppointmentModel> findByIdAndPsychologistId(Long id, Long psychologistId);

    Optional<AppointmentModel> findByPayment_Id(Long paymentId);
}
