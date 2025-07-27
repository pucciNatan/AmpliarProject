package com.example.ampliar.model;

import com.example.ampliar.validation.constraints.AppointmentDate;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data da consulta é obrigatória")
    @AppointmentDate
    private LocalDateTime appointmentDate;

    @NotNull(message = "O psicólogo é obrigatório")
    @OneToOne
    @JoinColumn(name = "psychologist_id", nullable = false)
    private PsychologistModel psychologist;

    @NotNull(message = "O paciente é obrigatório")
    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientModel patient;

    @Valid
    @NotNull(message = "O pagamento é obrigatório")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentModel payment;

    // Setters simples, sem validação duplicada
    public void setId(Long id) {
        this.id = id;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setPsychologist(PsychologistModel psychologist) {
        this.psychologist = psychologist;
    }

    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }

    public void setPayment(PaymentModel payment) {
        this.payment = payment;
    }
}
