package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        if (appointmentDate == null) {
            log.error("Tentativa de definir data de agendamento como nula");
            throw new IllegalArgumentException("Data do agendamento é obrigatória");
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
}