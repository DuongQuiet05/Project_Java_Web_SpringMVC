package ra.edu.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ra.edu.dto.auth.LoginAccountDTO;
import ra.edu.entity.Account;

import java.util.Optional;

public interface LoginHandlerService {
    Optional<Account> authenticate(LoginAccountDTO loginDTO);
    boolean handleRememberMe(Account account, String rememberMe, HttpServletResponse response);
    void storeSession(HttpServletRequest request, Account account);
    boolean checkRememberCookies(HttpServletRequest request);
}
