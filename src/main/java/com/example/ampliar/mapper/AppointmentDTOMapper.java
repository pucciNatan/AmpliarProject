package com.example.ampliar.mapper;

import com.example.ampliar.dto.appointment.AppointmentDTO;
import com.example.ampliar.model.AppointmentModel;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AppointmentDTOMapper implements Function<AppointmentModel, AppointmentDTO> {

    @Override
    public AppointmentDTO apply(AppointmentModel model) {
        return new AppointmentDTO(
                model.getId(),
                model.getAppointmentDate(),
                model.getPsychologist() != null ? model.getPsychologist().getId() : null,
                model.getPatient() != null ? model.getPatient().getId() : null,
                model.getPayment() != null ? model.getPayment().getId() : null
        );
    }
}