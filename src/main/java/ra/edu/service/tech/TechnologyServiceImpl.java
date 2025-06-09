package ra.edu.service.tech;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.edu.dto.tech.CreateTechnologyDTO;
import ra.edu.dto.tech.TechnologyListPageDTO;
import ra.edu.dto.tech.UpdateTechnologyDTO;
import ra.edu.entity.Technology;
import ra.edu.repository.tech.TechnologyRepo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TechnologyServiceImpl implements TechnologyService{
    private final TechnologyRepo technologyRepo;


    @Override
    public Optional<Technology> findByIdAllStatus(int id) {
        return technologyRepo.findByIdAllStatus(id);
    }

    @Override
    public List<Technology> getAllTechs() {
        return technologyRepo.getAllTechs();
    }

    @Override
    public Optional<Technology> findByNameAllStatus(String name) {
        return technologyRepo.findByNameAllStatus(name);
    }

    @Override
    public boolean createTech(CreateTechnologyDTO dto) {
        return technologyRepo.insertTech(convertCreateDTOtoTech(dto));
    }

    @Override
    public boolean updateTech(UpdateTechnologyDTO dto) {
        Optional<Technology> technologyOptional = technologyRepo.findByIdAllStatus(dto.getId());
        if (technologyOptional.isEmpty()) {
            System.err.println("SERVICE-ERROR: Cannot find id to update tech!");
            return false;
        }

        Technology newInfo = convertUpdateDTOtoTech(dto);
        Technology oldInfo = technologyOptional.get();

        if (!Objects.equals(newInfo.getName(), oldInfo.getName())) {
            oldInfo.setName(newInfo.getName());
        }
        if (!Objects.equals(newInfo.getImage(), oldInfo.getImage())) {
            oldInfo.setImage(newInfo.getImage());
        }
        if (!Objects.equals(newInfo.getDescription(), oldInfo.getDescription())) {
            oldInfo.setDescription(newInfo.getDescription());
        }

        return technologyRepo.updateTech(oldInfo);
    }

    @Override
    public boolean disableTech(int id) {
        return technologyRepo.disableTech(id);
    }

    @Override
    public boolean enableTech(int id) {
        return technologyRepo.enableTech(id);
    }



    @Override
    public List<Technology> getPaginateTechs(int page, int size) {
        return technologyRepo.getPaginateTechs(page,size);
    }

    @Override
    public int countTech() {
        return technologyRepo.countTech();
    }


    @Override
    public List<Technology> searchTechsByKeyword(String name, int page, int size) {
        return technologyRepo.searchTechsByKeyword(name,page,size);
    }

    @Override
    public int countTechForSearch(String name) {
        return technologyRepo.countTechForSearch(name);
    }

    // helper method
    @Override
    public Technology convertCreateDTOtoTech (CreateTechnologyDTO dto){
        Technology t = new Technology();
        t.setName(dto.getName());
        t.setImage(dto.getImage());
        t.setDescription(dto.getDescription());
        return t;
    }

    @Override
    public Technology convertUpdateDTOtoTech (UpdateTechnologyDTO dto){
        Technology t = new Technology();
        t.setId(dto.getId());
        t.setName(dto.getName());
        t.setImage(dto.getImage());
        t.setDescription(dto.getDescription());
        return t;
    }


    @Override
    public UpdateTechnologyDTO convertTechToUpdateDTO(Technology technology) {
        UpdateTechnologyDTO dto = new UpdateTechnologyDTO();
        dto.setId(technology.getId());
        dto.setName(technology.getName());
        dto.setImage(technology.getImage());
        dto.setDescription(technology.getDescription());
        return dto;
    }

    @Override
    public TechnologyListPageDTO getTechnologyListPage(int page, int size, String keyword) {
        List<Technology> technologies;
        int totalTechs;

        if (keyword != null && !keyword.trim().isEmpty()) {
            technologies = searchTechsByKeyword(keyword, page, size);
            totalTechs = countTechForSearch(keyword);
        } else {
            technologies = getPaginateTechs(page, size);
            totalTechs = countTech();
        }

        int totalPages = (int) Math.ceil((double) totalTechs / size);

        TechnologyListPageDTO dto = new TechnologyListPageDTO();
        dto.setTechnologies(technologies);
        dto.setCurrentPage(page);
        dto.setTotalPages(totalPages);
        dto.setSize(size);
        dto.setKeyword(keyword);

        return dto;
    }

}
