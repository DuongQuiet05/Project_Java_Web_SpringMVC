package ra.edu.validate.application;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class InterviewDateImpl implements ConstraintValidator<InterviewDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) return true;

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return !date.isBefore(tomorrow);
    }
}
