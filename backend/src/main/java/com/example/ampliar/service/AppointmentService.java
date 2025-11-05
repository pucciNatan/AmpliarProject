package com.example.ampliar.service;

import com.example.ampliar.dto.appointment.AppointmentDTO;
import com.example.ampliar.dto.appointment.AppointmentCreateDTO;
import com.example.ampliar.dto.appointment.AppointmentUpdateDTO;
import com.example.ampliar.mapper.AppointmentDTOMapper;
import com.example.ampliar.model.AppointmentModel;
import com.example.ampliar.model.PatientModel;
import com.example.ampliar.model.PaymentModel;
import com.example.ampliar.model.PsychologistModel;
import com.example.ampliar.model.enums.AppointmentStatus;
import com.example.ampliar.repository.AppointmentRepository;
import com.example.ampliar.repository.PatientRepository;
import com.example.ampliar.repository.PaymentRepository;
import com.example.ampliar.repository.PsychologistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PsychologistRepository psychologistRepository;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;
    private final AppointmentDTOMapper mapper;

    @Transactional
    public AppointmentDTO createAppointment(AppointmentCreateDTO dto) {
        log.info("Criando agendamento para psicólogo ID: {}", dto.psychologistId());

        try {
            PsychologistModel psych = psychologistRepository.findById(dto.psychologistId())
                    .orElseThrow(() -> {
                        log.error("Psicólogo não encontrado ID: {}", dto.psychologistId());
                        return new EntityNotFoundException("Psicólogo não encontrado");
                    });

            List<PatientModel> patients = patientRepository.findAllById(dto.patientIds());
            if (patients.size() != dto.patientIds().size()) {
                log.error("Pacientes não encontrados. Esperados: {}, Encontrados: {}",
                         dto.patientIds().size(), patients.size());
                throw new EntityNotFoundException("Há paciente(s) inexistente(s) no payload");
            }

            validatePsychologistAvailability(dto.appointmentDate(), psych.getId());
            for (PatientModel p : patients) {
                validatePatientAvailability(dto.appointmentDate(), p.getId());
            }

            // ✅ CORREÇÃO: Pagamento opcional
            PaymentModel payment = null;
            if (dto.paymentId() != null) {
                payment = paymentRepository.findById(dto.paymentId())
                        .orElseThrow(() -> {
                            log.error("Pagamento não encontrado ID: {}", dto.paymentId());
                            return new EntityNotFoundException("Pagamento não encontrado");
                        });
                log.debug("Pagamento associado: {}", dto.paymentId());
            } else {
                log.debug("Agendamento criado sem pagamento associado");
            }

            AppointmentModel model = new AppointmentModel();
            model.setAppointmentDate(dto.appointmentDate());
            model.setAppointmentEndDate(dto.appointmentEndDate());
            model.setAppointmentType(dto.type().trim());
            model.setNotes(normalizeNotes(dto.notes()));
            model.setStatus(dto.status() != null ? dto.status() : AppointmentStatus.SCHEDULED);
            model.setPsychologist(psych);
            model.setPatients(patients);
            model.setPayment(payment);

            model = appointmentRepository.save(model);
            log.info("Agendamento criado com sucesso ID: {}", model.getId());
            return mapper.apply(model);

        } catch (EntityNotFoundException e) {
            log.error("Erro ao criar agendamento - recurso não encontrado: {}", e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            log.warn("Conflito de agendamento: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar agendamento", e);
            throw new RuntimeException("Erro interno do servidor", e);
        }
    }

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentUpdateDTO dto) {
        log.info("Atualizando agendamento ID: {}", id);

        try {
            AppointmentModel model = appointmentRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Agendamento não encontrado ID: {}", id);
                        return new EntityNotFoundException("Agendamento não encontrado");
                    });

            if (dto.appointmentDate() != null) {
                validatePsychologistAvailability(dto.appointmentDate(), model.getPsychologist().getId());
                for (PatientModel p : model.getPatients()) {
                    validatePatientAvailability(dto.appointmentDate(), p.getId());
                }
                model.setAppointmentDate(dto.appointmentDate());
                log.debug("Data do agendamento atualizada");
            }

            if (dto.appointmentEndDate() != null) {
                model.setAppointmentEndDate(dto.appointmentEndDate());
                log.debug("Término do agendamento atualizado");
            }

            if (dto.type() != null) {
                model.setAppointmentType(dto.type().trim());
                log.debug("Tipo do agendamento atualizado");
            }

            if (dto.notes() != null) {
                model.setNotes(normalizeNotes(dto.notes()));
                log.debug("Observações do agendamento atualizadas");
            }

            if (dto.status() != null) {
                model.setStatus(dto.status());
                log.debug("Status do agendamento atualizado");
            }

            if (dto.psychologistId() != null && !dto.psychologistId().equals(model.getPsychologist().getId())) {
                PsychologistModel psych = psychologistRepository.findById(dto.psychologistId())
                        .orElseThrow(() -> {
                            log.error("Psicólogo não encontrado ID: {}", dto.psychologistId());
                            return new EntityNotFoundException("Psicólogo não encontrado");
                        });
                validatePsychologistAvailability(model.getAppointmentDate(), psych.getId());
                model.setPsychologist(psych);
                log.debug("Psicólogo do agendamento atualizado");
            }

            if (dto.patientIds() != null && !dto.patientIds().isEmpty()) {
                List<PatientModel> patients = patientRepository.findAllById(dto.patientIds());
                if (patients.size() != dto.patientIds().size()) {
                    log.error("Pacientes não encontrados na atualização. Esperados: {}, Encontrados: {}",
                             dto.patientIds().size(), patients.size());
                    throw new EntityNotFoundException("Há paciente(s) inexistente(s) no payload");
                }
                for (PatientModel p : patients) {
                    validatePatientAvailability(model.getAppointmentDate(), p.getId());
                }
                model.setPatients(patients);
                log.debug("Pacientes do agendamento atualizados");
            }

            // ✅ CORREÇÃO: Pagamento opcional
            if (dto.paymentId() != null) {
                PaymentModel payment = paymentRepository.findById(dto.paymentId())
                        .orElseThrow(() -> {
                            log.error("Pagamento não encontrado ID: {}", dto.paymentId());
                            return new EntityNotFoundException("Pagamento não encontrado");
                        });
                model.setPayment(payment);
                log.debug("Pagamento do agendamento atualizado");
            } else if (dto.paymentId() == null && model.getPayment() != null) {
                // Permite remover o pagamento definindo como null
                model.setPayment(null);
                log.debug("Pagamento removido do agendamento");
            }

            model = appointmentRepository.save(model);
            log.info("Agendamento atualizado com sucesso ID: {}", id);
            return mapper.apply(model);

        } catch (Exception e) {
            log.error("Erro ao atualizar agendamento ID: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public void deleteAppointment(Long id) {
        log.info("Excluindo agendamento ID: {}", id);
        try {
            if (!appointmentRepository.existsById(id)) {
                log.warn("Tentativa de excluir agendamento inexistente ID: {}", id);
                throw new EntityNotFoundException("Agendamento não encontrado");
            }
            appointmentRepository.deleteById(id);
            log.info("Agendamento excluído com sucesso ID: {}", id);
        } catch (Exception e) {
            log.error("Erro ao excluir agendamento ID: {}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        log.debug("Buscando agendamento por ID: {}", id);
        try {
            AppointmentModel model = appointmentRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Agendamento não encontrado ID: {}", id);
                        return new EntityNotFoundException("Agendamento não encontrado");
                    });
            log.debug("Agendamento encontrado ID: {}", id);
            return mapper.apply(model);
        } catch (Exception e) {
            log.error("Erro ao buscar agendamento ID: {}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAllAppointments() {
        log.debug("Buscando todos os agendamentos");
        try {
            var appointments = appointmentRepository.findAll()
                    .stream()
                    .map(mapper)
                    .collect(Collectors.toList());
            log.debug("Encontrados {} agendamentos", appointments.size());
            return appointments;
        } catch (Exception e) {
            log.error("Erro ao buscar todos os agendamentos", e);
            throw e;
        }
    }

    private void validatePsychologistAvailability(LocalDateTime date, Long psychologistId) {
        log.debug("Validando disponibilidade do psicólogo ID: {} para data: {}", psychologistId, date);
        boolean conflict = appointmentRepository
                .existsByAppointmentDateAndPsychologistId(date, psychologistId);
        if (conflict) {
            log.warn("Conflito de horário para psicólogo ID: {} na data: {}", psychologistId, date);
            throw new IllegalStateException("O psicólogo já tem um agendamento nesse horário");
        }
    }

    private void validatePatientAvailability(LocalDateTime date, Long patientId) {
        log.debug("Validando disponibilidade do paciente ID: {} para data: {}", patientId, date);
        boolean conflict = appointmentRepository
                .existsByAppointmentDateAndPatients_Id(date, patientId);
        if (conflict) {
            log.warn("Conflito de horário para paciente ID: {} na data: {}", patientId, date);
            throw new IllegalStateException("O paciente já tem um agendamento nesse horário");
        }
    }

    private String normalizeNotes(String notes) {
        if (notes == null) {
            return null;
        }
        String trimmed = notes.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
