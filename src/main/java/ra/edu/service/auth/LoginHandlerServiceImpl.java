package ra.edu.service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.edu.dto.auth.LoginAccountDTO;
import ra.edu.dto.auth.TokenWithExpiryDTO;
import ra.edu.entity.Account;
import ra.edu.entity.enums.account.Role;
import ra.edu.service.account.AccountService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginHandlerServiceImpl implements LoginHandlerService {
    private final AccountService accountService;
    private final RememberTokenService rememberTokenService;

    @Override
    public Optional<Account> authenticate(LoginAccountDTO loginDTO) {
        if (!accountService.login(loginDTO)) return Optional.empty();
        return accountService.findByEmailAllStatus(loginDTO.getEmail());
    }

    @Override
    public boolean handleRememberMe(Account account, String rememberMe, HttpServletResponse response) {
        if (!"on".equals(rememberMe)) return true;

        Optional<TokenWithExpiryDTO> tokenData = rememberTokenService.stayLoggedIn(account);
        if (tokenData.isEmpty()) return false;

        TokenWithExpiryDTO token = tokenData.get();
        long seconds = Duration.between(LocalDateTime.now(), token.getExpiry()).getSeconds();

        Cookie cookie = new Cookie("remember_token", token.getToken());
        cookie.setMaxAge((int) seconds);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return true;
    }

    @Override
    public void storeSession(HttpServletRequest request, Account account) {
        if (account.getRole() == Role.ADMIN) {
            request.getSession().setAttribute("adminStay", account);
        } else {
            request.getSession().setAttribute("candidateStay", account);
        }
    }

    @Override
    public boolean checkRememberCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;

        for (Cookie cookie : cookies) {
            if ("remember_token".equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }

}
