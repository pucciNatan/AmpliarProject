package com.example.ampliar.validation.constraints;

import com.example.ampliar.validation.BirthDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthDateValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthDate {
    String message() default "Data de nascimento inválida: deve ser anterior à data atual e representar uma idade realista (até 130 anos)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
