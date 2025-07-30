package com.example.ampliar.validation;

import com.example.ampliar.validation.constraints.AppointmentDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class AppointmentDateValidator implements ConstraintValidator<AppointmentDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime appointmentDate, ConstraintValidatorContext context) {
        if (appointmentDate == null) {
            return false;
        }

        return appointmentDate.isEqual(LocalDateTime.now()) || appointmentDate.isAfter(LocalDateTime.now());
    }
}
