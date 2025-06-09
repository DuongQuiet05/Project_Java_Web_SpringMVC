package ra.edu.controller.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ra.edu.entity.Account;

@ControllerAdvice
public class GlobalAccountController {

    @ModelAttribute("adminStay")
    public Account addCurrentAdmin(HttpSession session) {
        return (Account) session.getAttribute("adminStay");
    }

    @ModelAttribute("candidateStay")
    public Account addCurrentCandidate(HttpSession session) {
        return (Account) session.getAttribute("candidateStay");
    }
}
