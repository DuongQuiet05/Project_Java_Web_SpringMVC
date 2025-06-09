package ra.edu.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LogoutHandleService {
    void deleteRememberTokenIfExist(HttpServletResponse response, HttpServletRequest request);
}
