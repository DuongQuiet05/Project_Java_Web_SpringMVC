package ra.edu.validate.auth;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ra.edu.repository.account.AccountRepo;

@Component
@RequiredArgsConstructor
public class UniqueEmailImpl implements ConstraintValidator<UniqueEmail, String> {

    private final AccountRepo accountRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return accountRepository.findByEmailAllStatus(email).isEmpty();
    }
}
