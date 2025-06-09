package ra.edu.controller.admin;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.candidate.CandidateDisplayDTO;
import ra.edu.dto.candidate.CandidateFilterDTO;
import ra.edu.entity.Account;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.service.account.AccountService;
import ra.edu.service.auth.AccountSessionService;
import ra.edu.service.tech.TechnologyService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/candidates")
public class AdminCandidateController {
    private final AccountService accountService;
    private final TechnologyService technologyService;
    private final AccountSessionService accountSessionService;


    @GetMapping
    public String showCandidateList(@ModelAttribute CandidateFilterDTO filterDTO,
                                    @RequestParam(name = "page", defaultValue = "1") int page,
                                    @RequestParam(name = "size", defaultValue = "5") int size,
                                    @RequestParam(name = "success", required = false) Boolean success,
                                    Model model,
                                    HttpSession session) {

        Optional<Account> accOpt = accountSessionService.checkAdminLogin(session);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        List<CandidateDisplayDTO> candidates = accountService.getFilteredCandidates(filterDTO, page, size);
        int totalCandidates = accountService.countFilteredCandidates(filterDTO);

        int totalPages = (int) Math.ceil((double) totalCandidates / size);

        model.addAttribute("candidates", candidates);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);

        model.addAttribute("technologies", technologyService.getAllTechs());
        model.addAttribute("filterDTO", filterDTO); // Gán object filter

        if (Boolean.TRUE.equals(success)) {
            model.addAttribute("showSuccess", true);
        }

        return "admin/candidate/admin_candidate_list";
    }


    @PostMapping("/change-status/{id}")
    public String changeCandidateStatus(@PathVariable("id") int id,
                                        @RequestParam("status") String status,
                                        RedirectAttributes redirectAttributes) {

        try {
            AccountStatus newStatus = AccountStatus.valueOf(status.toUpperCase());
            boolean result = accountService.changeStatus(id, newStatus);

            if (result) {
                redirectAttributes.addAttribute("success", true);
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("success", false);
        }

        return "redirect:/dashboard/candidates";
    }


//    @PostMapping("/reset-password/{id}")
//    public String resetCandidatePassword(@PathVariable("id") int id,
//                                         RedirectAttributes redirectAttributes,
//                                         @RequestParam(name = "page", defaultValue = "1") int page,
//                                         @RequestParam(name = "size", defaultValue = "5") int size) {
//        ResetPasswordResult result = accountService.resetPassword(id);
//
//        if (result.isSuccess()) {
//            redirectAttributes.addFlashAttribute("resetSuccess", true);
//            redirectAttributes.addFlashAttribute("resetEmail", result.getEmail());
//            redirectAttributes.addFlashAttribute("newPassword", result.getNewPassword());
//        } else {
//            redirectAttributes.addFlashAttribute("resetSuccess", false);
//        }
//
//        return "redirect:/dashboard/candidates?page=" + page + "&size=" + size;
//    }


}
