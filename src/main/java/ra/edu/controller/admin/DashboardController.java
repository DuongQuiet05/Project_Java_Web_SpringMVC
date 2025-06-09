package ra.edu.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.edu.entity.Account;
import ra.edu.entity.enums.account.Role;
import ra.edu.service.auth.AccountSessionService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private final AccountSessionService accountSessionService;

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(session);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

//        Account acc = accOpt.get();
        return "admin/admin_dashboard";
    }


}
