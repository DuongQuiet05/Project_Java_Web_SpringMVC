package ra.edu.controller.candidate;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.dto.candidate.ChangePasswordCandidateDTO;
import ra.edu.dto.candidate.DisplayProfileDTO;
import ra.edu.dto.candidate.UpdateCandidateProfileDTO;
import ra.edu.entity.Account;
import ra.edu.service.account.AccountService;
import ra.edu.service.auth.AccountSessionService;
import ra.edu.service.tech.TechnologyService;
import ra.edu.utils.HandlerFile;

import java.util.Optional;

@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class CandidateHomeController {

    private final AccountService accountService;
    private final TechnologyService technologyService;
    private final AccountSessionService accountSessionService;
    private final HandlerFile handlerFile;

    @GetMapping
    public String showHome() {
        return "index";
    }

    @GetMapping("/profile")
    public String showCandidateInfoForm(HttpSession session, Model model) {
        Optional<Account> accOpt = accountSessionService.checkCandidateLogin(session);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        Account acc = accOpt.get();

        Optional<Account> opt = accountService.findByIdWithTech(acc.getId());
        if (opt.isEmpty()) return "error";

        DisplayProfileDTO profileDTO = accountService.convertToDisplayProfileDTO(opt.get());

        model.addAttribute("profileDTO", profileDTO);
        return "candidate/info/candidate_profile";
    }


    @GetMapping("/edit-profile")
    public String showCandidateEditForm(HttpSession session, Model model) {
        Optional<Account> accOpt = accountSessionService.checkCandidateLogin(session);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        Account acc = accOpt.get();

        Optional<Account> opt = accountService.findByIdWithTech(acc.getId());
        if (opt.isEmpty()) return "error";

        UpdateCandidateProfileDTO dto = accountService.convertToUpdateCandidateProfileDTO(opt.get());

        model.addAttribute("updateDTO", dto);
        model.addAttribute("techList", technologyService.getAllTechs());
        return "candidate/info/candidate_edit_profile";
    }


    @PostMapping("/edit-profile")
    public String handleCandidateEdit(
            @Valid @ModelAttribute("updateDTO") UpdateCandidateProfileDTO dto,
            BindingResult result,
            HttpSession session,
            Model model
    ) {
        System.err.println("File class: " + (dto.getFile() == null ? "null" : dto.getFile().getClass().getName()));
        if (result.hasErrors()) {
            Optional<Account> accOpt = accountService.findByIdAllStatus(dto.getId());
            accOpt.ifPresent(acc -> dto.setAvatar(acc.getAvatar()));
            model.addAttribute("techList", technologyService.getAllTechs());
            return "candidate/info/candidate_edit_profile";
        }

        try {
            MultipartFile file = dto.getFile();
            if (file != null && !file.isEmpty()) {
                String filename = handlerFile.saveImage(file);
                dto.setAvatar(filename);
            } else {
                // Giữ lại avatar cũ
                Optional<Account> accOpt = accountService.findByIdAllStatus(dto.getId());
                accOpt.ifPresent(acc -> dto.setAvatar(acc.getAvatar()));
            }

            if (!accountService.updateAccount(dto)) {
                System.err.println("CONTROLLER-ERROR: Failed to update candidate");
                return "error";
            }

            // cập nhật session
            Optional<Account> updatedAcc = accountService.findByIdAllStatus(dto.getId());
            updatedAcc.ifPresent(acc -> session.setAttribute("candidateStay", acc));

        } catch (IllegalArgumentException e) {
            result.rejectValue("file", null, e.getMessage());
            return "candidate/info/candidate_edit_profile";
        } catch (Exception e) {
            System.err.println("CONTROLLER-ERROR while updating candidate profile: " + e.getMessage());
            return "error";
        }

        return "redirect:/home/profile?success=true";
    }


    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkCandidateLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        ChangePasswordCandidateDTO dto = new ChangePasswordCandidateDTO();
        dto.setAccountId(accOpt.get().getId());
        model.addAttribute("changePasswordDTO", dto);
        return "candidate/info/candidate_change_password";
    }

    @PostMapping("/change-password")
    public String handleChangePassword(@Valid @ModelAttribute("changePasswordDTO") ChangePasswordCandidateDTO dto,BindingResult result) {
        if (result.hasErrors()) {
            return "candidate/info/candidate_change_password";
        }
        if (!accountService.changePassword(dto)) {
            System.err.println("CONTROLLER-ERROR: Failed to change password candidate");
            return "candidate/info/candidate_change_password";
        }
        return "redirect:/home/profile?success=true";
    }
}
