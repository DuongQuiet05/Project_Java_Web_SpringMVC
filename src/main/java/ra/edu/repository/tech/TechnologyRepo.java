package ra.edu.repository.tech;


import ra.edu.entity.Technology;

import java.util.List;
import java.util.Optional;

public interface TechnologyRepo {
    // todo: chung --------------------------------------------------------------
    Optional<Technology> findByIdAllStatus(int id);

    Optional<Technology> findByNameAllStatus(String name);

    //todo: admin ----------------------------------------------------------------------------
    boolean insertTech(Technology technology);
    List<Technology> getAllTechs ();

    boolean updateTech(Technology technology);

    boolean disableTech(int id);
    boolean enableTech(int id);

    List<Technology> getPaginateTechs(int page, int size);

    int countTech();

    List<Technology> searchTechsByKeyword(String name, int page, int size);

    int countTechForSearch(String name);
    //todo: candidate ----------------------------------------------------------------------------



}
