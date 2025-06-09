package ra.edu.controller.admin;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.dto.tech.CreateTechnologyDTO;
import ra.edu.dto.tech.TechnologyListPageDTO;
import ra.edu.dto.tech.UpdateTechnologyDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Technology;
import ra.edu.service.auth.AccountSessionService;
import ra.edu.service.tech.TechnologyService;
import ra.edu.utils.HandlerFile;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard/technologies")
public class AdminTechnologyController {
    private final TechnologyService technologyService;
    private final AccountSessionService accountSessionService;
    private final HandlerFile handlerFile;

    @GetMapping
    public String showTechnologyList(
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

        TechnologyListPageDTO listPageDTO = technologyService.getTechnologyListPage(page, size, keyword);

        model.addAttribute("technologies", listPageDTO.getTechnologies());
        model.addAttribute("currentPage", listPageDTO.getCurrentPage());
        model.addAttribute("totalPages", listPageDTO.getTotalPages());
        model.addAttribute("size", listPageDTO.getSize());
        model.addAttribute("keyword", listPageDTO.getKeyword());

        if (Boolean.TRUE.equals(success)) {
            model.addAttribute("showSuccess", true);
        }

        return "admin/tech/admin_tech_list";
    }


    @GetMapping("/add")
    public String showAddForm (Model model, HttpSession httpSession){
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        model.addAttribute("createTechDTO",new CreateTechnologyDTO());
        return "admin/tech/admin_tech_add";
    }


    @PostMapping("/add")
    public String handleAdd (@Valid @ModelAttribute("createTechDTO") CreateTechnologyDTO dto, BindingResult result) {
        if (result.hasErrors()) return "admin/tech/admin_tech_add";

        try {
            String filename = handlerFile.saveImage(dto.getFile());
            dto.setImage(filename);

            if (!technologyService.createTech(dto)) {
                return "error";
            }
        }
        catch (IllegalArgumentException e) {
            result.rejectValue("file", null, e.getMessage());
            return "admin/tech/admin_tech_add";
        }

        return "redirect:/dashboard/technologies?page=1&size=5&success=true";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm (@PathVariable("id") int id, Model model, HttpSession httpSession){
        Optional<Account> accOpt = accountSessionService.checkAdminLogin(httpSession);
        if (accOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        Optional<Technology> technologyOptional = technologyService.findByIdAllStatus(id);
        if(technologyOptional.isEmpty()){
            System.err.println("CONTROLLER-ERROR: Cannot find id to update tech!");
            return "error";
        }

        model.addAttribute("updateTechDTO", technologyService.convertTechToUpdateDTO(technologyOptional.get()));
        return "admin/tech/admin_tech_edit";
    }


    @PostMapping("/edit")
    public String handleEdit(@Valid @ModelAttribute("updateTechDTO") UpdateTechnologyDTO dto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            Optional<Technology> techOpt = technologyService.findByIdAllStatus(dto.getId());
            techOpt.ifPresent(tech -> dto.setImage(tech.getImage()));
            return "admin/tech/admin_tech_edit";
        }

        try {
            MultipartFile file = dto.getFile();
            if (file != null && !file.isEmpty()) {
                String filename = handlerFile.saveImage(file);
                dto.setImage(filename);
            } else {
                // Giữ lại ảnh cũ
                Optional<Technology> techOpt = technologyService.findByIdAllStatus(dto.getId());
                techOpt.ifPresent(tech -> dto.setImage(tech.getImage()));
            }

            if (!technologyService.updateTech(dto)) {
                return "error";
            }
        }
        catch (IllegalArgumentException e) {
            result.rejectValue("file", null, e.getMessage());
            return "admin/tech/admin_tech_edit";
        }
        catch (Exception e) {
            System.err.println("CONTROLLER-ERROR when updating tech: " + dto.getName() + " - " + e.getMessage());
            return "error";
        }

        return "redirect:/dashboard/technologies?page=1&size=5&success=true";
    }

    @PostMapping("/delete/{id}")
    public String handleDelete (@PathVariable("id") int id, HttpSession httpSession){

        if(!technologyService.disableTech(id)){
            System.err.println("CONTROLLER-ERROR: Cannot delete tech!");
            return "admin/tech/admin_tech_list";
        }
        return "redirect:/dashboard/technologies?page=1&size=5";
    }


    @PostMapping("/enable/{id}")
    public String handleEnable(@PathVariable("id") int id){
        if(!technologyService.enableTech(id)){
            System.err.println("CONTROLLER-ERROR: Cannot enable tech!");
            return "admin/tech/admin_tech_list";
        }
        return "redirect:/dashboard/technologies?page=1&size=5";
    }





}
