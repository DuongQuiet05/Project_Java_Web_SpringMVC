package ra.edu.controller.admin;


import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.dto.position.CreatePositionDTO;
import ra.edu.dto.position.PositionDisplayDTO;
import ra.edu.dto.position.UpdatePositionDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Position;
import ra.edu.service.auth.AccountSessionService;
import ra.edu.service.location.LocationService;
import ra.edu.service.position.PositionService;
import ra.edu.service.tech.TechnologyService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/positions")
public class AdminPositionController {

    private final PositionService positionService;
    private final AccountSessionService accountSessionService;
    private final TechnologyService technologyService;
    private final LocationService locationService;


    @GetMapping
    public String showPositionList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "success", required = false) Boolean success,
            Model model,
            HttpSession session) {

        Optional<Account> accOpt = accountSessionService.checkAdminLogin(session);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        List<PositionDisplayDTO> positions;
        int total;

        if (keyword != null && !keyword.trim().isEmpty()) {
            positions = positionService.search(keyword, page, size);
            total = positionService.countSearch(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            positions = positionService.getPaginate(page, size);
            total = positionService.countAll();
        }

        int totalPages = (int) Math.ceil((double) total / size);

        model.addAttribute("positions", positions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        if (Boolean.TRUE.equals(success)) {
            model.addAttribute("showSuccess", true);
        }

        return "admin/position/admin_position_list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        model.addAttribute("createPositionDTO", new CreatePositionDTO());
        model.addAttribute("technologies", technologyService.getAllTechs());
        model.addAttribute("locations", locationService.getAllLocations());
        return "admin/position/admin_position_add";
    }


    @PostMapping("/add")
    public String handleAdd(
            @Valid @ModelAttribute("createPositionDTO") CreatePositionDTO dto,
            BindingResult result, Model model) {

        if (result.hasErrors()) {
            System.err.println("Errors: " + result.getAllErrors());
            model.addAttribute("technologies", technologyService.getAllTechs());
            model.addAttribute("locations", locationService.getAllLocations());
            return "admin/position/admin_position_add";
        }

        try {
            if (!positionService.create(dto)) {
                return "error";
            }
        } catch (Exception e) {
            System.err.println("CONTROLLER-ERROR while creating position: " + e.getMessage());
            return "error";
        }

        return "redirect:/dashboard/positions?success=true";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        Optional<Position> opt = positionService.findByIdAllStatus(id);
        if (opt.isEmpty()) return "error";

        Position p = opt.get();
        UpdatePositionDTO dto = positionService.convertToUpdateDto(p);

        model.addAttribute("updatePositionDTO", dto);
        model.addAttribute("technologies", technologyService.getAllTechs());
        model.addAttribute("locations", locationService.getAllLocations());
        return "admin/position/admin_position_edit";
    }



    @PostMapping("/edit")
    public String handleEdit(
            @Valid @ModelAttribute("updatePositionDTO") UpdatePositionDTO dto,
            BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("technologies", technologyService.getAllTechs());
            model.addAttribute("locations", locationService.getAllLocations());
            model.addAttribute("formattedDate", dto.getExpiredDate().toString());
            return "admin/position/admin_position_edit";
        }

        try {
            if (!positionService.update(dto)) {
                return "error";
            }
        } catch (Exception e) {
            System.err.println("CONTROLLER-ERROR while updating position: " + e.getMessage());
            return "error";
        }

        return "redirect:/dashboard/positions?success=true";
    }


    @PostMapping("/delete/{id}")
    public String handleDelete(@PathVariable("id") int id, HttpSession httpSession) {
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        if (!positionService.delete(id)) {
            System.err.println("CONTROLLER-ERROR: Delete failed!");
        }
        return "redirect:/dashboard/positions?success=true";
    }

}
