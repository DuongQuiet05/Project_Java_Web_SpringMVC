package ra.edu.repository.position;

import ra.edu.entity.weeks.PositionTechnology;

import java.util.List;

public interface PositionTechnologyRepo {
    List<PositionTechnology> findByRecruitmentIds(List<Integer> ids);

    boolean insertAll(List<PositionTechnology> list);

    boolean deleteByPositionId(int positionId);
}
