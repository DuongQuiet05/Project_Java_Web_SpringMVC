package ra.edu.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ra.edu.dto.position.candidate.PositionListForCandidateDTO;
import ra.edu.entity.Location;
import ra.edu.repository.account.AccountRepo;
import ra.edu.service.location.LocationService;
import ra.edu.service.position.PositionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {
    private final PositionService positionService;
    private final LocationService locationService;

    @GetMapping("/")
    public String goIndex(HttpSession session, Model model) {
        List<PositionListForCandidateDTO> positions = positionService.getPositionForYou();
        model.addAttribute("positionsForYou", positions);

        List<Location> locations = locationService.getAllLocations();
        model.addAttribute("locations", locations);

        return "index";
    }
}
