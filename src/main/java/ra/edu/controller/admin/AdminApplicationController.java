package ra.edu.controller.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.application.*;
import ra.edu.entity.Account;
import ra.edu.entity.Application;
import ra.edu.entity.enums.application.CandidateConfirmStatus;
import ra.edu.service.application.ApplicationService;
import ra.edu.service.auth.AccountSessionService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/applications")
public class AdminApplicationController {
    private final ApplicationService applicationService;
    private final AccountSessionService accountSessionService;

    @GetMapping
    public String showApplicationList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @ModelAttribute("filterDTO") ApplicationFilterDTO filterDTO,
            @RequestParam(name = "success", required = false) Boolean success,
            Model model,
            HttpSession session) {

        Optional<Account> accOpt = accountSessionService.checkAdminLogin(session);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        List<ApplicationListDTO> filteredApplications = applicationService.filterApplications(filterDTO, page, size);
        int totalFiltered = applicationService.countFilteredApplications(filterDTO);
        int totalPages = (int) Math.ceil((double) totalFiltered / size);

        model.addAttribute("applications", filteredApplications);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("filterDTO", filterDTO);

        if (Boolean.TRUE.equals(success)) {
            model.addAttribute("showSuccess", true);
        }

        return "admin/application/admin_application_list";
    }


    @GetMapping("/{id}")
    public String showApplicationDetail(@PathVariable("id") int id, Model model, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        ApplicationDetailDTO dto = applicationService.getDetail(id,accOpt.get());
        model.addAttribute("appDetail", dto);
        return "admin/application/admin_application_detail";
    }

    @GetMapping("/{id}/interview")
    public String showInterviewScheduleForm(@PathVariable("id") int appId, Model model, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        Optional<Application> optApp = applicationService.findById(appId);
        if (optApp.isEmpty()) {
            return "error";
        }

        Application app = optApp.get();

        ApplicationInterviewDTO dto = new ApplicationInterviewDTO(); // cho vào service
        dto.setId(app.getId());
        dto.setInterviewUrl(app.getInterviewUrl());
        dto.setConfirmStatus(app.getCandidateConfirmed());

        if (app.getInterviewDate() != null) {
            dto.setInterviewDate(app.getInterviewDate().toLocalDate());
            dto.setInterviewTime(app.getInterviewDate().toLocalTime());
        }

        model.addAttribute("interviewDTO", dto);
        model.addAttribute("appId", appId);

        return "admin/application/admin_application_interview";
    }

    @PostMapping("/{id}/interview")
    public String handleInterviewSchedule(
            @PathVariable("id") int appId,
            @ModelAttribute("interviewDTO") @Validated ApplicationInterviewDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirect
    ) {
        model.addAttribute("appId", appId);

        if (result.hasErrors()) {
            return "admin/application/admin_application_interview";
        }

        if (dto.getConfirmStatus() == CandidateConfirmStatus.NOT_CONFIRMED) {
            // Ứng viên chưa xác nhận → cho đặt lịch
            boolean updated = applicationService.updateInterviewSchedule(appId, dto);
            if (!updated) {
                System.err.println("CONTROLLER-ERROR Không thể lưu lịch hẹn.");
                return "error";
            }
//            redirect.addFlashAttribute("success", "Lịch hẹn đã được cập nhật. Chờ ứng viên xác nhận.");

        } else {
            // Ứng viên đã xác nhận → chỉ nhập link
            boolean linkUpdated = applicationService.updateInterviewUrl(appId, dto.getInterviewUrl());
            System.err.println("link" + dto.getInterviewUrl());
            if (!linkUpdated) {
                System.err.println("CONTROLLER-ERROR: KHÔNG LƯU ĐƯỢC LINK PHỎNG VẤN");
                return "error";
            }
            System.err.println("link ok");
            // Sau khi lưu link → thử chuyển progress nếu đủ điều kiện
            boolean progressed = applicationService.updateProgressToInterviewing(appId);
            if (!progressed) {
//                redirect.addFlashAttribute("success", "Link đã lưu. Ứng viên đã được chuyển sang INTERVIEWING.");
                System.err.println("CONTROLLER-ERROR: LINK ĐÃ LƯU NHƯNG KHÔNG ĐỦ ĐIỀU KIỆN CHUYỂN INTERVIEW");
                return "error";
            }
        }

        return "redirect:/dashboard/applications/" + appId;
    }


    @GetMapping("/{id}/reject")
    public String showRejectForm(@PathVariable("id") int appId, Model model, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        model.addAttribute("rejectDTO", new ApplicationRejectDTO());
        model.addAttribute("appId", appId);
        return "admin/application/admin_application_reject";
    }


    @PostMapping("/{id}/reject")
    public String rejectApplication(
            @PathVariable("id") int appId,
            @ModelAttribute("rejectDTO") @Validated ApplicationRejectDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirect
    ) {
        model.addAttribute("appId", appId);
        if (result.hasErrors()) {
            return "admin/application/admin_application_reject";
        }

        boolean rejected = applicationService.rejectApplication(appId, dto);

        if (!rejected) {
            System.err.println("CONTROLLER-ERROR: Không thể từ chối đơn.");
            return "error";
        }

        redirect.addFlashAttribute("success", "Reject successfully!");
        return "redirect:/dashboard/applications/" + appId;
    }

    @GetMapping("/{id}/result")
    public String showResultForm(@PathVariable("id") int appId, Model model, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        ApplicationResultDTO dto = new ApplicationResultDTO();
        applicationService.checkInterviewTime(appId,dto);

        model.addAttribute("appId", appId);
        model.addAttribute("resultDTO",dto);
        return "admin/application/admin_application_result";
    }

    @PostMapping("/{id}/result")
    public String handleResultUpdate(
            @PathVariable("id") int appId,
            @RequestParam("action") String action,
            @ModelAttribute("resultDTO") @Validated ApplicationResultDTO dto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("appId", appId);
            return "admin/application/admin_application_result";
        }

        boolean updated = applicationService.updateApplicationResult(appId, action, dto);

        if (!updated) {
            System.err.println("CONTROLLER-ERROR: Không thể cập nhật kết quả");
            return "error";
        }

        return "redirect:/dashboard/applications/" + appId;
    }


}
