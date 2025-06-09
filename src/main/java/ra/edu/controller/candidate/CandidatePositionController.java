package ra.edu.controller.candidate;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.application.ApplyApplicationDTO;
import ra.edu.dto.position.candidate.PositionDetailForCandidateDTO;
import ra.edu.dto.position.candidate.PositionListForCandidateDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Position;
import ra.edu.service.application.ApplicationService;
import ra.edu.service.auth.AccountSessionService;
import ra.edu.service.location.LocationService;
import ra.edu.service.position.PositionService;
import ra.edu.utils.HandlerFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/home/positions")
public class CandidatePositionController {
    private final PositionService positionService;
    private final AccountSessionService accountSessionService;
    private final HandlerFile handlerFile;
    private final ApplicationService applicationService;
    private final LocationService locationService;

    @GetMapping
    public String showPositionList (
            Model model,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "9") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "city", required = false) String city
    ){
        List<PositionListForCandidateDTO> positions;

        if ((keyword != null && !keyword.trim().isEmpty()) || (city != null && !city.trim().isEmpty())) {
            positions = positionService.searchActiveForCandidateByKeywordAndLocation(keyword, city, page, size);
        } else {
            positions = positionService.findAllActiveForCandidate(page, size);
        }

        int totalPositions = ((keyword != null && !keyword.trim().isEmpty()) || (city != null && !city.trim().isEmpty()))
                ? positionService.countAllByKeywordAndLocation(keyword, city)
                : positionService.countAll();

        int totalPages = (int) Math.ceil((double) totalPositions / size);

        model.addAttribute("rpListDTO", positions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);
        model.addAttribute("city", city);
        model.addAttribute("locations", locationService.getAllLocations());

        return "candidate/position/candidate_position_list";
    }


    @GetMapping("/{id}")
    public String showPositionDetail(@PathVariable("id") int id, Model model) {
        Optional<PositionDetailForCandidateDTO> dtoOpt = positionService.getPositionDetail(id);
        if (dtoOpt.isEmpty()) {
            System.err.println("CONTROLLER-ERROR: KHÔNG THẤY VỊ TRÍ ĐỂ XEM CHI TIẾT.");
            return "error";
        }

        List<PositionListForCandidateDTO> positions = positionService.getRecommendedPosition();

        model.addAttribute("detailPosition", dtoOpt.get());
        model.addAttribute("recommends",positions);
        return "candidate/position/candidate_position_detail";
    }

    @GetMapping("/{id}/apply")
    public String showApplyForm (@PathVariable("id") int id, Model model, HttpSession httpSession, RedirectAttributes redirectAttributes){
        Optional<Account> accOpt = accountSessionService.checkCandidateLogin(httpSession);
        if (accOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("notLoggedIn", true);
            return "redirect:/home/positions/" + id;
        }

        Optional<Position> positionOptional = positionService.findByIdAllStatus(id);
        if (positionOptional.isEmpty()) {
            System.err.println("CONTROLLER-ERROR: KHÔNG THẤY VỊ TRÍ ĐỂ ỨNG TUYỂN");
            return "error";
        }

        ApplyApplicationDTO dto = positionService.convertToApplyDTO(positionOptional.get(), accOpt.get());

        model.addAttribute("applyDTO",dto);

        return "candidate/application/candidate_apply_position";
    }

    @PostMapping("/{id}/apply")
    public String handleApply(@PathVariable("id") int id,
                              @Valid @ModelAttribute("applyDTO") ApplyApplicationDTO dto,
                              BindingResult result,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "candidate/application/candidate_apply_position";
        }

        Optional<Account> accOpt = accountSessionService.checkCandidateLogin(session);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        try {
            String cvUrl = handlerFile.savePdf(dto.getFile());
            dto.setCvURL(cvUrl);
            dto.setPositionId(id);

            if (!applicationService.createApplication(dto, accOpt.get())) {
                return "error";
            }

            redirectAttributes.addFlashAttribute("success", "Apply successfully!");
        } catch (IllegalArgumentException e) {
            result.rejectValue("file", null, e.getMessage());
            return "candidate/application/candidate_apply_position";
        }

        return "redirect:/home/positions/" + id;
    }



}
