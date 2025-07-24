package com.example.ampliar.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "appointment")
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appointmentDate;

    @OneToOne
    @JoinColumn(name = "psychologist_id")
    private PsychologistModel psychologist;

    @OneToOne
    @JoinColumn(name = "patient_id")
    private PatientModel patient;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_id")
    private PaymentModel payment;

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo");
        }
        this.id = id;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        if (appointmentDate == null) {
            throw new IllegalArgumentException("A data da consulta é obrigatória");
        }
        if (appointmentDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data da consulta deve ser futura");
        }
        this.appointmentDate = appointmentDate;
    }

    public void setPsychologist(PsychologistModel psychologist) {
        if (psychologist == null) {
            throw new IllegalArgumentException("O psicólogo não pode ser nulo");
        }
        this.psychologist = psychologist;
    }

    public void setPatient(PatientModel patient) {
        if (patient == null) {
            throw new IllegalArgumentException("O paciente não pode ser nulo");
        }
        this.patient = patient;
    }

    public void setPayment(PaymentModel payment) {
        if (payment == null) {
            throw new IllegalArgumentException("O pagamento não pode ser nulo");
        }
        this.payment = payment;
    }
}
