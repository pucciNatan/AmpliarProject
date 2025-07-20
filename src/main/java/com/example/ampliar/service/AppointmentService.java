package com.example.ampliar.service;

import com.example.ampliar.model.AppointmentModel;
import com.example.ampliar.repository.AppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public AppointmentModel createAppointment(AppointmentModel appointment) {
        return appointmentRepository.save(appointment);
    }

    public AppointmentModel updateAppointment(Long id, AppointmentModel updatedAppointment) {
        AppointmentModel existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        existingAppointment.setAppointmentDate(updatedAppointment.getAppointmentDate());
        existingAppointment.setPatient(updatedAppointment.getPatient());
        existingAppointment.setPsychologist(updatedAppointment.getPsychologist());
        existingAppointment.setPayment(updatedAppointment.getPayment());

        return appointmentRepository.save(existingAppointment);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }

    public AppointmentModel getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
    }

    public List<AppointmentModel> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}
