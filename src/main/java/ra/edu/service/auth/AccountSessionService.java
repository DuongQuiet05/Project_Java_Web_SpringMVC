package ra.edu.service.auth;

import jakarta.servlet.http.HttpSession;
import ra.edu.entity.Account;

import java.util.Optional;

public interface AccountSessionService {
    Optional<Account> checkAdminLogin(HttpSession session);
    Optional<Account> checkCandidateLogin(HttpSession session);
}
