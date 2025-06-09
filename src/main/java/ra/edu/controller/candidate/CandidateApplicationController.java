package ra.edu.controller.candidate;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.application.ApplicationCancelDTO;
import ra.edu.dto.application.ApplicationDetailDTO;
import ra.edu.dto.application.ApplicationRejectDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Application;
import ra.edu.entity.enums.application.CandidateConfirmStatus;
import ra.edu.service.application.ApplicationService;
import ra.edu.service.auth.AccountSessionService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/home/applications")
public class CandidateApplicationController {

    private final ApplicationService applicationService;
    private final AccountSessionService accountSessionService;

    @GetMapping
    public String showApplicationList(Model model,
                                      HttpSession session,
                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                      @RequestParam(name = "size", defaultValue = "9") int size) {

        Optional<Account> accOpt = accountSessionService.checkCandidateLogin(session);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        int accountId = accOpt.get().getId();

        List<Application> applications = applicationService.getAllApplicationByAccountId(accountId, page, size);
        int totalApplications = applicationService.countAllApplicationByAccountId(accountId);
        int totalPages = (int) Math.ceil((double) totalApplications / size);

        model.addAttribute("applications", applications);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "candidate/application/candidate_application_list";
    }

    @GetMapping("/{id}")
    public String showApplicationDetail(@PathVariable("id") int id, Model model, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkCandidateLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        ApplicationDetailDTO dto = applicationService.getDetail(id,accOpt.get());
        model.addAttribute("appDetail", dto);
        return "candidate/application/candidate_application_detail";
    }
    @PostMapping("/{id}/confirm")
    public String handleConfirm (@PathVariable("id") int id){
        applicationService.updateCandidateConfirmStatus(id, CandidateConfirmStatus.CONFIRMED);
        return "redirect:/home/applications/" + id;
    }


    @GetMapping("/{id}/cancel")
    public String showCancelForm(@PathVariable("id") int appId, Model model) {
        model.addAttribute("cancelDTO", new ApplicationCancelDTO());
        model.addAttribute("appId", appId);
        return "candidate/application/candidate_application_cancel";
    }


    @PostMapping("/{id}/cancel")
    public String cancelApplication(
            @PathVariable("id") int appId,
            @ModelAttribute("cancelDTO") @Validated ApplicationCancelDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirect
    ) {
        model.addAttribute("appId", appId);
        if (result.hasErrors()) {
            return "candidate/application/candidate_application_cancel";
        }

        boolean canceled = applicationService.cancelApplication(appId, dto);

        if (!canceled) {
            System.err.println("CONTROLLER-ERROR: Không thể hủy đơn.");
            return "error";
        }

        redirect.addFlashAttribute("success", "cancel successfully!");
        return "redirect:/home/applications/" + appId;
    }


}
