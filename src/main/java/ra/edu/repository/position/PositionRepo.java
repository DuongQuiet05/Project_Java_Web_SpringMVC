package ra.edu.repository.position;


import ra.edu.entity.Position;

import java.util.List;
import java.util.Optional;

public interface PositionRepo {

    // todo : chung -------------------------------------------------------------------------
    Optional<Position> findById(int id);

    Optional<Position> findByName(String name);

    Optional<Position> findByIdAllStatus(int id);

    Optional<Position> findByNameAllStatus(String name);

    //todo: admin --------------------------------------------------------------------------
    boolean insert(Position position);
    Optional<Position> getLastIndex ();

    boolean update(Position position);

    boolean delete(int id);


    List<Position> getPaginate(int page, int size);

    int countAll();

    List<Position> searchByKeyword(String keyword, int page, int size);

    int countByKeyword(String keyword);

    // todo : candidate ---------------------------------------------------------------------
    List<Position> findAllActiveForCandidate(int offset, int limit);
    // dùng chung countAll để đếm phân trang

    List<Position> searchActiveForCandidateByKeyword(String keyword, int page, int size);

    int countActiveForCandidateByKeyword(String keyword);
    Optional<Position> findByIdWithTechnologies(int id);
    List<Position> findSomeActiveForCandidate ();
    List<Position> findPositionForYou();
    List<Position> searchActiveForCandidateByKeywordAndLocation(String keyword, String city, int page, int size);
    int countAllByKeywordAndLocation(String keyword, String city);
}
