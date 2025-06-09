package ra.edu.utils;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ra.edu.entity.Account;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Role;
import ra.edu.service.account.AccountService;
import ra.edu.service.auth.RememberTokenService;

import java.util.Optional;

// xử lí lấy dữ liệu cookie ở controller get khi truy cập cũng đc nhưng nó chỉ hiệu quả nếu truy cập vào login nhưng nếu truy cập vào những trang khác thì lại không chạy qua nó và vô nghĩa.
// mọi request đều chạy qua interceptor đầu tiên nên có thể control chúng ở đây.
@Component
@RequiredArgsConstructor
public class RememberMeInterceptor implements HandlerInterceptor {

    private final RememberTokenService rememberTokenService;
    private final AccountService accountService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            if (!validateAndRefreshSessionAccount(request, session, response)) {
                return false;
            }
        } else {
            autoLoginByRememberToken(request, response);
        }

        return true;
    }

    /**
     * Validate account trong session:
     * - Lấy fresh account từ DB
     * - Check status
     * - Update session
     * return false nếu chặn request
     */
    private boolean validateAndRefreshSessionAccount(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        Optional<Account> accOpt = getSessionAccount(session);
        if (accOpt.isEmpty()) return true; // Không có account -> tiếp tục request

        Account accFound = accOpt.get();

        Optional<Account> freshAccOpt = accountService.findByIdAllStatus(accFound.getId());
        if (freshAccOpt.isEmpty()) {
            return invalidateAndRedirect(session, response, "/auth/login");
        }

        Account freshAcc = freshAccOpt.get();
        if (freshAcc.getStatus() == AccountStatus.DISABLE) {
            deleteRememberToken(request, response);
            return invalidateAndRedirect(session, response, "/auth/login?locked=true");
        }

        // Update session
        setSessionAccount(session, freshAcc);
        return true;
    }

    /**
     * Tự động login nếu có remember_token cookie
     */
    private void autoLoginByRememberToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;

        for (Cookie cookie : cookies) {
            if ("remember_token".equals(cookie.getName())) {
                String token = cookie.getValue();
                Optional<Account> accOpt = rememberTokenService.findAccountByToken(token);
                if (accOpt.isPresent()) {
                    Account acc = accOpt.get();
                    HttpSession newSession = request.getSession(true);
                    setSessionAccount(newSession, acc);

                    try {
                        if (acc.getRole() == Role.ADMIN) {
                            response.sendRedirect("/dashboard");
                        } else {
                            response.sendRedirect("/");
                        }
                    } catch (Exception e) {
                        System.err.println("AUTOLOGIN-REDIRECT-ERROR: " + e.getMessage());
                    }
                }
                break;
            }
        }
    }

    private Optional<Account> getSessionAccount(HttpSession session) {
        Account acc = (Account) session.getAttribute("adminStay");
        if (acc != null) return Optional.of(acc);

        acc = (Account) session.getAttribute("candidateStay");
        return Optional.ofNullable(acc);
    }

    private void setSessionAccount(HttpSession session, Account acc) {
        if (acc.getRole() == Role.ADMIN) {
            session.setAttribute("adminStay", acc);
        } else {
            session.setAttribute("candidateStay", acc);
        }
    }

    private boolean invalidateAndRedirect(HttpSession session, HttpServletResponse response, String path) {
        session.invalidate();
        try {
            response.sendRedirect(path);
        } catch (Exception e) {
            System.err.println("INTERCEPTOR-REDIRECT-ERROR (" + path + "): " + e.getMessage());
        }
        return false;
    }

    private void deleteRememberToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember_token".equals(cookie.getName())) {
                    String token = cookie.getValue();

                    // Xóa cookie phía client
                    Cookie deleteCookie = new Cookie("remember_token", "");
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setPath("/");
                    response.addCookie(deleteCookie);

                    // Xóa token trong DB
                    boolean deleted = rememberTokenService.deleteTokenOnLogout(token);
                    if (!deleted) {
                        System.err.println("DELETE-TOKEN-ERROR: Failed to delete token in DB.");
                    }

                    break;
                }
            }
        }
    }

}

