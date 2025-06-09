package ra.edu.service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutHandleServiceImpl implements LogoutHandleService {
    private final RememberTokenService rememberTokenService;

    @Override
    public void deleteRememberTokenIfExist(HttpServletResponse response, HttpServletRequest request) {
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
