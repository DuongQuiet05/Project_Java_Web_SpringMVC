package ra.edu.validate.candidate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ra.edu.dto.candidate.ChangePasswordCandidateDTO;
import ra.edu.entity.Account;
import ra.edu.service.account.AccountService;

import java.util.Optional;
@RequiredArgsConstructor
public class ValidOldPasswordImpl implements ConstraintValidator<ValidOldPassword, ChangePasswordCandidateDTO> {
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(ChangePasswordCandidateDTO dto, ConstraintValidatorContext context) {
        String oldPassword = dto.getOldPassword();
        if (oldPassword == null || oldPassword.isBlank()) {
            return true;
        }// đẩy cho not blank

        // Check dữ liệu cơ bản
        if (dto.getAccountId() == 0) {
            return false;
        }

        Optional<Account> accOpt = accountService.findByIdAllStatus(dto.getAccountId());
        if (accOpt.isEmpty()) {
            return false;
        }

        Account acc = accOpt.get();
        String storedPassword = acc.getPassword();

        boolean match;

        if (storedPassword != null && storedPassword.startsWith("$2a$")) {
            // Nếu là hash BCrypt
            match = passwordEncoder.matches(oldPassword, storedPassword);
        } else {
            // Nếu là plaintext (dành cho tài khoản cũ)
            match = oldPassword.equals(storedPassword);
        }

        if (!match) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("oldPassword")
                    .addConstraintViolation();
        }

        return match;
    }
}
