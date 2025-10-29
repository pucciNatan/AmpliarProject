package com.example.ampliar.validation;

import java.time.LocalDate;
import java.time.Period;

import com.example.ampliar.validation.constraints.BirthDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {

    private static final int MAX_AGE = 130;

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        if (birthDate.isAfter(today)) {
            return false;
        }

        int age = Period.between(birthDate, today).getYears();
        return age <= MAX_AGE;
    }
}
