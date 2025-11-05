package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.ampliar.model.enums.AppointmentStatus;

@Entity
@Table(name = "appointment")
@Getter
@NoArgsConstructor
@Slf4j
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "psychologist_id", nullable = false)
    private PsychologistModel psychologist;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "appointment_patients",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    private List<PatientModel> patients = new ArrayList<>();

    // ✅ CORREÇÃO: Pagamento opcional
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "payment_id", nullable = true, unique = false)
    private PaymentModel payment;

    @Column(name = "appointment_end_date")
    private LocalDateTime appointmentEndDate;

    @Column(name = "appointment_type", length = 100)
    private String appointmentType;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        if (appointmentDate == null) {
            log.error("Tentativa de definir data de agendamento como nula");
            throw new IllegalArgumentException("Data do agendamento é obrigatória");
        }
        if (this.appointmentEndDate != null && appointmentDate.isAfter(this.appointmentEndDate)) {
            log.error("Início do agendamento não pode ser posterior ao término: {} > {}", appointmentDate, this.appointmentEndDate);
            throw new IllegalArgumentException("A data do agendamento deve ser anterior ao término");
        }
        this.appointmentDate = appointmentDate;
        log.debug("Data do agendamento definida: {}", appointmentDate);
    }

    public void setPsychologist(PsychologistModel psychologist) {
        if (psychologist == null) {
            log.error("Tentativa de definir psicólogo como nulo");
            throw new IllegalArgumentException("Psicólogo é obrigatório");
        }
        this.psychologist = psychologist;
        log.debug("Psicólogo definido: {}", psychologist.getId());
    }

    public void setPatients(List<PatientModel> patients) {
        if (patients == null || patients.isEmpty()) {
            log.error("Tentativa de definir lista de pacientes vazia ou nula");
            throw new IllegalArgumentException("Informe pelo menos 1 paciente");
        }
        this.patients = patients;
        log.debug("{} pacientes definidos para o agendamento", patients.size());
    }

    public void setPayment(PaymentModel payment) {
        // ✅ CORREÇÃO: Pagamento pode ser nulo
        this.payment = payment;
        if (payment != null) {
            log.debug("Pagamento definido para agendamento: {}", payment.getId());
        } else {
            log.debug("Agendamento definido sem pagamento");
        }
    }

    public void setAppointmentEndDate(LocalDateTime appointmentEndDate) {
        if (appointmentEndDate != null && this.appointmentDate != null && appointmentEndDate.isBefore(this.appointmentDate)) {
            log.error("Tentativa de definir término antes do início: {} < {}", appointmentEndDate, this.appointmentDate);
            throw new IllegalArgumentException("A data de término deve ser posterior ao início do agendamento");
        }
        this.appointmentEndDate = appointmentEndDate;
        if (appointmentEndDate != null) {
            log.debug("Término do agendamento definido: {}", appointmentEndDate);
        }
    }

    public void setAppointmentType(String appointmentType) {
        if (appointmentType != null && appointmentType.length() > 100) {
            log.error("Tentativa de definir tipo de agendamento com mais de 100 caracteres");
            throw new IllegalArgumentException("O tipo da consulta deve ter no máximo 100 caracteres");
        }
        this.appointmentType = appointmentType != null ? appointmentType.trim() : null;
        if (appointmentType != null) {
            log.debug("Tipo de agendamento definido: {}", appointmentType);
        }
    }

    public void setNotes(String notes) {
        if (notes != null && notes.length() > 1000) {
            log.error("Tentativa de definir observações com mais de 1000 caracteres");
            throw new IllegalArgumentException("As observações devem ter no máximo 1000 caracteres");
        }
        this.notes = notes != null ? notes.trim() : null;
        if (notes != null) {
            log.debug("Observações definidas para o agendamento");
        }
    }

    public void setStatus(AppointmentStatus status) {
        if (status == null) {
            log.error("Tentativa de definir status nulo para o agendamento");
            throw new IllegalArgumentException("O status do agendamento é obrigatório");
        }
        this.status = status;
        log.debug("Status do agendamento definido: {}", status);
    }
}
