package ra.edu.repository.position;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ra.edu.entity.weeks.PositionTechnology;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class PositionTechnologyRepoImpl implements PositionTechnologyRepo {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<PositionTechnology> findByRecruitmentIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        return getSession().createQuery("""
            from PositionTechnology rpt
            join fetch rpt.technology
            where rpt.position.id in (:ids)
        """, PositionTechnology.class)
                .setParameter("ids", ids)
                .list();
    }

    @Override
    public boolean insertAll(List<PositionTechnology> list) {
        try {
            for (PositionTechnology techLink : list) {
                getSession().merge(techLink);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Insert link failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteByPositionId(int positionId) {
        try {
            getSession().createMutationQuery("""
            delete from PositionTechnology
            where position.id = :positionId
        """)
                    .setParameter("positionId", positionId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete tech links for position " + positionId + ": " + e.getMessage());
            return false;
        }
    }
}
