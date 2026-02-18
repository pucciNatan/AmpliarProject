package com.example.ampliar.controller;

import com.example.ampliar.dto.appointment.AppointmentCreateDTO;
import com.example.ampliar.dto.appointment.AppointmentDTO;
import com.example.ampliar.dto.appointment.AppointmentUpdateDTO;
import com.example.ampliar.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentCreateDTO appointmentDTO) {
        log.info("Recebida requisição POST /appointments - Criar agendamento - Psicólogo: {}, Pacientes: {}, Pagamento: {}",
                 appointmentDTO.psychologistId(), appointmentDTO.patientIds().size(), appointmentDTO.paymentId());
        AppointmentDTO result = appointmentService.createAppointment(appointmentDTO);
        log.info("Agendamento criado com sucesso - ID: {}, Data: {}", result.id(), result.appointmentDate());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateDTO updatedDTO) {
        log.info("Recebida requisição PUT /appointments/{} - Atualizar agendamento", id);
        AppointmentDTO result = appointmentService.updateAppointment(id, updatedDTO);
        log.info("Agendamento atualizado com sucesso - ID: {}", id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        log.info("Recebida requisição DELETE /appointments/{} - Excluir agendamento", id);
        appointmentService.deleteAppointment(id);
        log.info("Agendamento excluído com sucesso - ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        log.debug("Recebida requisição GET /appointments/{} - Buscar agendamento por ID", id);
        AppointmentDTO result = appointmentService.getAppointmentById(id);
        log.debug("Agendamento encontrado - ID: {}, Data: {}", id, result.appointmentDate());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        log.debug("Recebida requisição GET /appointments - Listar todos os agendamentos");
        List<AppointmentDTO> result = appointmentService.getAllAppointments();
        log.debug("Lista de agendamentos retornada - Total: {}", result.size());
        return ResponseEntity.ok(result);
    }
}
