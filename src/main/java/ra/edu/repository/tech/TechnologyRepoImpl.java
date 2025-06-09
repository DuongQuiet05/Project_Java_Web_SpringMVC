package ra.edu.repository.tech;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Technology;
import ra.edu.entity.enums.tech.TechnologyStatus;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class TechnologyRepoImpl implements TechnologyRepo{
    private final SessionFactory sessionFactory;

    private Session getSession (){
        return sessionFactory.getCurrentSession();
    }

    // chỉ với active----------------------------------------------------
    @Override
    public boolean insertTech(Technology technology) {
        try {
            getSession().persist(technology);
            return true;
        }catch (Exception e){
            System.err.println("REPO-ERROR" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateTech(Technology technology) {
        try {
            getSession().merge(technology);
            return true;
        }catch (Exception e){
            System.err.println("REPO-ERROR" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean disableTech(int id) {
        MutationQuery query = getSession().createMutationQuery("update Technology set status = :p_status where id = :p_id")
                .setParameter("p_id",id)
                .setParameter("p_status", TechnologyStatus.DISABLED);
        return query.executeUpdate() > 0;
    }

    @Override
    public boolean enableTech(int id) {
        MutationQuery query = getSession().createMutationQuery("update Technology set status = :p_status where id = :p_id")
                .setParameter("p_id",id)
                .setParameter("p_status", TechnologyStatus.ENABLED);
        return query.executeUpdate() > 0;
    }


    @Override
    public List<Technology> getPaginateTechs(int page, int size) {
        int firstResult = (page - 1) * size;
        return getSession().createQuery("from Technology order by id desc",Technology.class
        ).setFirstResult(firstResult)
                .setMaxResults(size)
                .list();
    }

    @Override
    public int countTech() {
        return getSession()
                .createQuery("select count(t) from Technology t", Long.class)
                .uniqueResultOptional()
                .map(count -> count.intValue())
                .orElse(0);
    }


    @Override
    public List<Technology> searchTechsByKeyword(String name, int page, int size) {
        int firstResult = (page - 1) * size;
        return getSession().createQuery("from Technology " +
                        "where name like concat('%',:p_name,'%') ", Technology.class)
                .setParameter("p_name",name)
                .setFirstResult(firstResult)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public int countTechForSearch(String name) {
        return getSession()
                .createQuery("select count(t.id) from Technology t where t.name like concat('%',:p_name,'%')", Long.class)
                .setParameter("p_name", name)
                .uniqueResultOptional()
                .map(count -> count.intValue())
                .orElse(0);

    }

    @Override
    public List<Technology> getAllTechs() {
        return getSession().createQuery("from Technology where status = :p_status",Technology.class)
                .setParameter("p_status", TechnologyStatus.ENABLED)
                .list();
    }

    // với tất cả status----------------------------------------------------
    @Override
    public Optional<Technology> findByIdAllStatus(int id) {
        return getSession().createQuery("from Technology where id = :p_id",Technology.class)
                .setParameter("p_id",id)
                .uniqueResultOptional();
    }

    @Override
    public Optional<Technology> findByNameAllStatus(String name) {
        return getSession().createQuery("from Technology where name = :p_name",Technology.class)
                .setParameter("p_name",name)
                .uniqueResultOptional();
    }
}
