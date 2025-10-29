package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "appointment")
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "psychologist_id", nullable = false)
    private PsychologistModel psychologist;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientModel patient;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentModel payment;

    public AppointmentModel(LocalDateTime appointmentDate, PsychologistModel psychologist,
                            PatientModel patient, PaymentModel payment) {
        setAppointmentDate(appointmentDate);
        setPsychologist(psychologist);
        setPatient(patient);
        setPayment(payment);
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("O ID não pode ser negativo");
        }
        this.id = id;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        if (appointmentDate == null) {
            throw new IllegalArgumentException("A data da consulta é obrigatória");
        }
        if (appointmentDate.isBefore(LocalDateTime.now().minusYears(10))) {
            throw new IllegalArgumentException("A data da consulta é muito antiga");
        }
        this.appointmentDate = appointmentDate;
    }

    public void setPsychologist(PsychologistModel psychologist) {
        if (psychologist == null) {
            throw new IllegalArgumentException("O psicólogo é obrigatório");
        }
        this.psychologist = psychologist;
    }

    public void setPatient(PatientModel patient) {
        if (patient == null) {
            throw new IllegalArgumentException("O paciente é obrigatório");
        }
        this.patient = patient;
    }

    public void setPayment(PaymentModel payment) {
        if (payment == null) {
            throw new IllegalArgumentException("O pagamento é obrigatório");
        }
        this.payment = payment;
    }
}
