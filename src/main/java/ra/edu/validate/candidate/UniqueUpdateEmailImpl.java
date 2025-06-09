package ra.edu.validate.candidate;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ra.edu.dto.candidate.UpdateCandidateProfileDTO;
import ra.edu.entity.Account;
import ra.edu.service.account.AccountService;

import java.util.Optional;

@RequiredArgsConstructor
public class UniqueUpdateEmailImpl implements ConstraintValidator<UniqueUpdateEmail, UpdateCandidateProfileDTO> {
    private final AccountService accountService;
    @Override
    public boolean isValid(UpdateCandidateProfileDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            return true; // để validator khác xử lý null/blank
        }
        Optional<Account> optional = accountService.findByEmailAllStatus(dto.getEmail());

        if (optional.isEmpty()) return true;

        Account existing = optional.get();

        if (existing.getId() != dto.getId()) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("email")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}