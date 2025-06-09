package ra.edu.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.auth.LoginAccountDTO;
import ra.edu.dto.auth.RegisterAccountDTO;
import ra.edu.entity.Account;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Role;
import ra.edu.service.account.AccountService;
import ra.edu.service.auth.LoginHandlerService;
import ra.edu.service.auth.LogoutHandleService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AccountService accountService;
    private final LoginHandlerService loginHandlerService;
    private final LogoutHandleService logoutHandleService;


    // todo: login logout register --------------------------------------------------------------------------------------
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        RegisterAccountDTO dto = new RegisterAccountDTO();
        model.addAttribute("registerDTO", dto);
        return "auth/register";
    }


    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerDTO") RegisterAccountDTO registerDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        boolean created = accountService.createAccount(registerDTO);
        if (!created) {
            return "error";
        }

        redirectAttributes.addFlashAttribute("registerSuccess", "Account registered successfully. Please login!");
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request) {
        if (!model.containsAttribute("LockedNotify")) {
            model.addAttribute("LockedNotify", null);
        }

        boolean hasRememberMe = loginHandlerService.checkRememberCookies(request);
        model.addAttribute("hasRememberMe", hasRememberMe);

        LoginAccountDTO dto = new LoginAccountDTO();
        model.addAttribute("loginDTO", dto);
        return "auth/login";
    }


    @PostMapping("/login")
    public String handleLogin(
            @Valid @ModelAttribute("loginDTO") LoginAccountDTO loginAccountDTO,
            BindingResult result,
            Model model,
            @RequestParam(value = "rememberMe", required = false) String rememberMe,
            HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean hasRememberMe = loginHandlerService.checkRememberCookies(request);
        if (result.hasErrors()) {

            model.addAttribute("hasRememberMe", hasRememberMe);
            return "auth/login";
        }

        Optional<Account> accOpt = loginHandlerService.authenticate(loginAccountDTO);
        if (accOpt.isEmpty()) {
            model.addAttribute("hasRememberMe", hasRememberMe);
            model.addAttribute("wrongInfo", "Invalid email or password.");
            return "auth/login";
        }

        Account acc = accOpt.get();


        if (acc.getStatus() == AccountStatus.DISABLE) {
            redirectAttributes.addFlashAttribute("LockedNotify", "Your account is locked. Please contact admin.");
            return "redirect:/auth/login";
        }

        if (!loginHandlerService.handleRememberMe(acc, rememberMe, response)) {
            System.err.println("CONTROLLER-ERROR: INSERT LOGIN TOKEN FAILED!");
            return "auth/login";
        }

        loginHandlerService.storeSession(request, acc);
        redirectAttributes.addFlashAttribute("loginSuccess", "Welcome back, " + acc.getName() + "!");

        return acc.getRole() == Role.ADMIN ? "redirect:/dashboard" : "redirect:/";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        logoutHandleService.deleteRememberTokenIfExist(response, request);

        return "redirect:/auth/login";
    }

}
