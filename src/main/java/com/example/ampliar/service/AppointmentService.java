package com.example.ampliar.service;

import com.example.ampliar.dto.AppointmentDTO;
import com.example.ampliar.dto.AppointmentCreateDTO;
import com.example.ampliar.dto.AppointmentUpdateDTO;
import com.example.ampliar.mapper.AppointmentDTOMapper;
import com.example.ampliar.model.AppointmentModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.model.PaymentModel;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.repository.AppointmentRepository;
import com.example.ampliar.repository.PatientRepository;
import com.example.ampliar.repository.PaymentRepository;
import com.example.ampliar.repository.PsychologistRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentDTOMapper appointmentDTOMapper;
    private final PatientRepository patientRepository;
    private final PsychologistRepository psychologistRepository;
    private final PaymentRepository paymentRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            AppointmentDTOMapper appointmentDTOMapper,
            PatientRepository patientRepository,
            PsychologistRepository psychologistRepository,
            PaymentRepository paymentRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentDTOMapper = appointmentDTOMapper;
        this.patientRepository = patientRepository;
        this.psychologistRepository = psychologistRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public AppointmentDTO createAppointment(AppointmentCreateDTO dto) {
        validateAppointmentDate(dto.appointmentDate());

        PsychologistModel psychologist = getPsychologist(dto.psychologistId());
        PatientModel patient = getPatient(dto.patientId());

        validatePsychologistAvailability(dto.appointmentDate(), psychologist.getId());
        validatePatientAvailability(dto.appointmentDate(), patient.getId());

        PaymentModel payment = (dto.paymentId() != null) ? getPayment(dto.paymentId()) : null;

        AppointmentModel model = new AppointmentModel();
        model.setAppointmentDate(dto.appointmentDate());
        model.setPsychologist(psychologist);
        model.setPatient(patient);
        model.setPayment(payment);

        return appointmentDTOMapper.apply(appointmentRepository.save(model));
    }

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentUpdateDTO dto) {
        AppointmentModel existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        validateAppointmentDate(dto.appointmentDate());

        if (!existing.getAppointmentDate().equals(dto.appointmentDate())
                || !existing.getPsychologist().getId().equals(dto.psychologistId())) {
            validatePsychologistAvailability(dto.appointmentDate(), dto.psychologistId());
        }

        if (!existing.getAppointmentDate().equals(dto.appointmentDate())
                || !existing.getPatient().getId().equals(dto.patientId())) {
            validatePatientAvailability(dto.appointmentDate(), dto.patientId());
        }

        existing.setAppointmentDate(dto.appointmentDate());
        existing.setPsychologist(getPsychologist(dto.psychologistId()));
        existing.setPatient(getPatient(dto.patientId()));
        existing.setPayment(dto.paymentId() != null ? getPayment(dto.paymentId()) : null);

        return appointmentDTOMapper.apply(appointmentRepository.save(existing));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Agendamento não encontrado");
        }
        appointmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(appointmentDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentDTOMapper)
                .collect(Collectors.toList());
    }

    private PatientModel getPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado"));
    }

    private PsychologistModel getPsychologist(Long id) {
        return psychologistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo não encontrado"));
    }

    private PaymentModel getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));
    }

    private void validateAppointmentDate(LocalDateTime date) {
        if (date == null) throw new IllegalArgumentException("Data da consulta obrigatória");
    }

    private void validatePsychologistAvailability(LocalDateTime date, Long psychologistId) {
        boolean conflict = appointmentRepository.existsByAppointmentDateAndPsychologistId(date, psychologistId);
        if (conflict) {
            throw new IllegalStateException("O psicólogo já tem um agendamento nesse horário");
        }
    }

    private void validatePatientAvailability(LocalDateTime date, Long patientId) {
        boolean conflict = appointmentRepository.existsByAppointmentDateAndPatientId(date, patientId);
        if (conflict) {
            throw new IllegalStateException("O paciente já tem um agendamento nesse horário");
        }
    }

}
