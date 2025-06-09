package ra.edu.validate.auth;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ra.edu.dto.auth.RegisterAccountDTO;

public class PasswordRegisterMatchesImpl implements ConstraintValidator<PasswordRegisterMatches, RegisterAccountDTO> {
    @Override
    public boolean isValid(RegisterAccountDTO dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null || dto.getConfirmPassword().isBlank()) {
            return true;
        }

        boolean match = dto.getPassword().equals(dto.getConfirmPassword());
        if (!match) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return match;
    }
}

