package ru.aston.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;

import java.util.Set;

public class ValidationUtil {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("; ");
            }
            throw new ValidationException("Validation failed: " + sb.toString());
        }
    }

    public static <T> Set<ConstraintViolation<T>> getViolations(T object) {
        return VALIDATOR.validate(object);
    }

    public static <T> boolean isValid(T object) {
        return VALIDATOR.validate(object).isEmpty();
    }
}
