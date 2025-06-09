package ra.edu.service.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import ra.edu.entity.Account;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Role;

import java.util.Optional;

@Service
public class AccountSessionServiceImpl implements AccountSessionService {
    @Override
    public Optional<Account> checkAdminLogin(HttpSession session) {
        Account acc = (Account) session.getAttribute("adminStay");
        if (acc == null || acc.getRole() != Role.ADMIN) {
            return Optional.empty();
        }
        return Optional.of(acc);
    }

    @Override
    public Optional<Account> checkCandidateLogin(HttpSession session) {
        Account acc = (Account) session.getAttribute("candidateStay");
        if (acc == null || acc.getRole() != Role.CANDIDATE || acc.getStatus() == AccountStatus.DISABLE) {
            return Optional.empty();
        }
        return Optional.of(acc);
    }

}
