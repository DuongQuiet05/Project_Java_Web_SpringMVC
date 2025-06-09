package ra.edu.validate.auth;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailFormatImpl implements ConstraintValidator<EmailFormat, String> {

    private static final String EMAIL =
            "^(?i)[a-z][a-z0-9]{4,}@gmail\\.com$";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || s.isBlank()) return true;

        return s.matches(EMAIL);
    }
}
