package com.example.ampliar.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.example.ampliar.validation.constraints.AppointmentDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @AppointmentDate
    private LocalDateTime appointmentDate;

    @OneToOne
    @NotNull
    @JoinColumn(name = "psychologist_id")
    private PsychologistModel psychologist;

    @OneToOne
    @NotNull
    @JoinColumn(name = "patient_id")
    private PatientModel patient;

    @Valid
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_id")
    private PaymentModel payment;
}
