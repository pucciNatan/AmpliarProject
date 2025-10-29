package com.example.ampliar.controller;

import com.example.ampliar.dto.appointment.AppointmentCreateDTO;
import com.example.ampliar.dto.appointment.AppointmentDTO;
import com.example.ampliar.dto.appointment.AppointmentUpdateDTO;
import com.example.ampliar.service.AppointmentService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public AppointmentDTO createAppointment(@Valid @RequestBody AppointmentCreateDTO appointmentDTO) {
        return appointmentService.createAppointment(appointmentDTO);
    }

    @PutMapping("/{id}")
    public AppointmentDTO updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateDTO updatedDTO) {
        return appointmentService.updateAppointment(id, updatedDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }

    @GetMapping("/{id}")
    public AppointmentDTO getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }
}
