package ra.edu.validate.candidate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ra.edu.dto.auth.RegisterAccountDTO;
import ra.edu.dto.candidate.ChangePasswordCandidateDTO;
import ra.edu.validate.auth.PasswordRegisterMatches;

public class ChangePasswordMatchesImpl implements ConstraintValidator<ChangePasswordMatches, ChangePasswordCandidateDTO> {
    @Override
    public boolean isValid(ChangePasswordCandidateDTO dto, ConstraintValidatorContext context) {
        if (dto.getNewPassword() == null || dto.getConfirmNewPassword() == null || dto.getConfirmNewPassword().isBlank()) {
            return true;
        }

        boolean match = dto.getNewPassword().equals(dto.getConfirmNewPassword());
        if (!match) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmNewPassword")
                    .addConstraintViolation();
        }

        return match;
    }
}
