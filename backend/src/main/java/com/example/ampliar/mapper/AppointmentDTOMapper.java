package com.example.ampliar.mapper;

import com.example.ampliar.dto.appointment.AppointmentDTO;
import com.example.ampliar.model.AppointmentModel;
import com.example.ampliar.model.PaymentModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class AppointmentDTOMapper implements Function<AppointmentModel, AppointmentDTO> {

    @Override
    public AppointmentDTO apply(AppointmentModel model) {
        log.debug("Mapeando AppointmentModel para DTO - ID: {}", model.getId());

        List<AppointmentDTO.PatientSummary> patients = model.getPatients() == null
                ? List.of()
                : model.getPatients().stream()
                        .map(p -> new AppointmentDTO.PatientSummary(p.getId(), p.getFullName()))
                        .toList();

        AppointmentDTO.PsychologistSummary psychologist = null;
        if (model.getPsychologist() != null) {
            psychologist = new AppointmentDTO.PsychologistSummary(
                    model.getPsychologist().getId(),
                    model.getPsychologist().getFullName()
            );
        }

        var payment = model.getPayment();
        Long paymentId = payment != null ? payment.getId() : null;
        BigDecimal paymentAmount = payment != null ? payment.getValor() : BigDecimal.ZERO;
        String paymentStatus = resolvePaymentStatus(model.getAppointmentDate(), model.getAppointmentEndDate(), payment);

        log.debug("Agendamento ID: {} - {} pacientes, pagamento: {}",
                 model.getId(), patients.size(), paymentId != null ? paymentId : "Nenhum");

        return new AppointmentDTO(
                model.getId(),
                model.getAppointmentDate(),
                model.getAppointmentEndDate(),
                model.getStatus(),
                model.getAppointmentType(),
                model.getNotes(),
                psychologist,
                patients,
                paymentStatus,
                paymentAmount,
                paymentId
        );
    }

    private String resolvePaymentStatus(LocalDateTime start, LocalDateTime end, PaymentModel payment) {
        if (payment != null) {
            return "paid";
        }

        LocalDateTime reference = end != null ? end : start;
        if (reference != null && reference.isBefore(LocalDateTime.now())) {
            return "overdue";
        }

        return "pending";
    }
}
