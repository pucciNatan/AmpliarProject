package com.example.ampliar.service;

import com.example.ampliar.dto.AppointmentDTO;
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
import org.springframework.stereotype.Service;

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

    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        AppointmentModel model = new AppointmentModel();
        model.setAppointmentDate(dto.appointmentDate());
        model.setPatient(getPatient(dto.patientId()));
        model.setPsychologist(getPsychologist(dto.psychologistId()));
        model.setPayment(getPayment(dto.paymentId()));

        return appointmentDTOMapper.apply(appointmentRepository.save(model));
    }

    public AppointmentDTO updateAppointment(Long id, AppointmentDTO dto) {
        AppointmentModel existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        existing.setAppointmentDate(dto.appointmentDate());
        existing.setPatient(getPatient(dto.patientId()));
        existing.setPsychologist(getPsychologist(dto.psychologistId()));
        existing.setPayment(getPayment(dto.paymentId()));

        return appointmentDTOMapper.apply(appointmentRepository.save(existing));
    }

    public AppointmentDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(appointmentDTOMapper)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentDTOMapper)
                .collect(Collectors.toList());
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Agendamento não encontrado");
        }
        appointmentRepository.deleteById(id);
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
}
