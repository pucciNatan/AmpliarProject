package com.example.ampliar.mapper;

import com.example.ampliar.dto.appointment.AppointmentDTO;
import com.example.ampliar.model.AppointmentModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class AppointmentDTOMapper implements Function<AppointmentModel, AppointmentDTO> {
    
    @Override
    public AppointmentDTO apply(AppointmentModel model) {
        log.debug("Mapeando AppointmentModel para DTO - ID: {}", model.getId());
        
        List<Long> patientIds = model.getPatients() == null
                ? List.of()
                : model.getPatients().stream().map(p -> p.getId()).toList();

        Long paymentId = model.getPayment() != null ? model.getPayment().getId() : null;
        
        log.debug("Agendamento ID: {} - {} pacientes, pagamento: {}", 
                 model.getId(), patientIds.size(), paymentId != null ? paymentId : "Nenhum");
        
        return new AppointmentDTO(
                model.getId(),
                model.getAppointmentDate(),
                model.getPsychologist() != null ? model.getPsychologist().getId() : null,
                patientIds,
                paymentId  // ✅ CORREÇÃO: Pode ser null
        );
    }
}