package com.example.ampliar.validation.constraints;

import com.example.ampliar.validation.AppointmentDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AppointmentDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AppointmentDate {
    String message() default "A data da consulta não deve ser no passado";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
