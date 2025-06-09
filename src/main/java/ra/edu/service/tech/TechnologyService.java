package ra.edu.service.tech;


import ra.edu.dto.tech.CreateTechnologyDTO;
import ra.edu.dto.tech.TechnologyListPageDTO;
import ra.edu.dto.tech.UpdateTechnologyDTO;
import ra.edu.entity.Technology;

import java.util.List;
import java.util.Optional;


public interface TechnologyService {
    // todo : chung ---------------------------------------------------------------------------
    Optional<Technology> findByIdAllStatus(int id);

    Optional<Technology> findByNameAllStatus(String name);
    List<Technology> getAllTechs ();
    // todo: admin ----------------------------------------------------------------------------
    TechnologyListPageDTO getTechnologyListPage(int page, int size, String keyword);

    boolean createTech(CreateTechnologyDTO dto);

    boolean updateTech(UpdateTechnologyDTO dto);

    boolean disableTech(int id);

    boolean enableTech(int id);

    List<Technology> getPaginateTechs(int page, int size);

    int countTech();

    List<Technology> searchTechsByKeyword(String name, int page, int size);

    int countTechForSearch(String name);

    // todo : convert -------------------------------------------------------------------------

    Technology convertCreateDTOtoTech(CreateTechnologyDTO dto);

    Technology convertUpdateDTOtoTech(UpdateTechnologyDTO dto);

    UpdateTechnologyDTO convertTechToUpdateDTO(Technology technology);
}
