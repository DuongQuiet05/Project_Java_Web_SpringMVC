package ra.edu.repository.position;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Position;
import ra.edu.entity.enums.position.PositionStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class PositionRepoImpl implements PositionRepo {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public boolean insert(Position position) {
        try {
            getSession().persist(position);
            return true;
        } catch (Exception e) {
            System.err.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Position> getLastIndex() {
        return getSession().createQuery(
                        "from Position order by id desc", Position.class)
                .setMaxResults(1)
                .uniqueResultOptional();
    }

    @Override
    public boolean update(Position position) {
        try {
            getSession().merge(position);
            return true;
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
            return false;
        }
    }




    @Override
    public boolean delete(int id) {
        String hql = "update Position set status = :status where id = :id";
        return getSession().createMutationQuery(hql)
                .setParameter("status", PositionStatus.DISABLED)
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Override
    public Optional<Position> findById(int id) {
        return getSession().createQuery(
                        "from Position where id = :id and status = :status", Position.class)
                .setParameter("id", id)
                .setParameter("status", PositionStatus.ENABLED)
                .uniqueResultOptional();
    }

    @Override
    public Optional<Position> findByName(String name) {
        return getSession().createQuery(
                        "from Position where name = :name and status = :status", Position.class)
                .setParameter("name", name)
                .setParameter("status", PositionStatus.ENABLED)
                .uniqueResultOptional();
    }

    @Override
    public List<Position> getPaginate(int page, int size) {
        int first = (page - 1) * size;
        return getSession().createQuery(
                        "from Position where status = :status order by createdAt desc", Position.class)
                .setParameter("status", PositionStatus.ENABLED)
                .setFirstResult(first)
                .setMaxResults(size)
                .list();
    }

    @Override
    public int countAll() {
        return getSession().createQuery(
                        "select count(r.id) from Position r where status = :status", Long.class)
                .setParameter("status", PositionStatus.ENABLED)
                .uniqueResultOptional()
                .map(count -> count.intValue())
                .orElse(0);
    }

    @Override
    public List<Position> searchByKeyword(String keyword, int page, int size) {
        int first = (page - 1) * size;
        return getSession().createQuery("""
                from Position
                where status = :status and lower(name) like concat('%', lower(:keyword), '%')
                order by createdAt desc
                """, Position.class)
                .setParameter("status", PositionStatus.ENABLED)
                .setParameter("keyword", keyword)
                .setFirstResult(first)
                .setMaxResults(size)
                .list();
    }

    @Override
    public int countByKeyword(String keyword) {
        Long count = getSession().createQuery("""
                select count(r.id)
                from Position r
                where status = :status and lower(name) like concat('%', lower(:keyword), '%')
                """, Long.class)
                .setParameter("status", PositionStatus.ENABLED)
                .setParameter("keyword", keyword)
                .uniqueResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Optional<Position> findByIdAllStatus(int id) {
        return getSession().createQuery("from Position where id = :id", Position.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Override
    public Optional<Position> findByNameAllStatus(String name) {
        return getSession().createQuery("from Position where name = :name", Position.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }


    // todo: candidate ----------------------------------------------------------------------

    @Override
    public List<Position> findAllActiveForCandidate(int offset, int limit) {
        int firstResult = (offset - 1) * limit;

        Query<Position> query = getSession()
                .createQuery("""
                        select distinct rp
                        from Position rp
                        left join fetch rp.positionTechnologies rpt
                        left join fetch rpt.technology t
                        where rp.status = :p_status
                        order by rp.createdAt desc
                        """,Position.class)
                .setParameter("p_status",PositionStatus.ENABLED);

        return query
                .setFirstResult(firstResult)
                .setMaxResults(limit)
                .list();
    }


    @Override
    public List<Position> searchActiveForCandidateByKeyword(String keyword, int page, int size) {
        int first = (page - 1) * size;
        return getSession().createQuery("""
        select distinct p
        from Position p
        left join fetch p.positionTechnologies pt
        where p.status = :status and lower(p.name) like concat('%', lower(:keyword), '%')
        order by p.createdAt desc
        """, Position.class)
                .setParameter("status", PositionStatus.ENABLED)
                .setParameter("keyword", keyword)
                .setFirstResult(first)
                .setMaxResults(size)
                .list();
    }


    @Override
    public int countActiveForCandidateByKeyword(String keyword) {
        Long count = getSession().createQuery("""
        select count(distinct p.id)
        from Position p
        left join p.positionTechnologies pt
        where p.status = :status and lower(p.name) like concat('%', lower(:keyword), '%')
        """, Long.class)
                .setParameter("status", PositionStatus.ENABLED)
                .setParameter("keyword", keyword)
                .uniqueResult();
        return count != null ? count.intValue() : 0;
    }


    @Override
    public Optional<Position> findByIdWithTechnologies(int id) {
        return getSession().createQuery("""
        select distinct p
        from Position p
        left join fetch p.positionTechnologies pt
        left join fetch pt.technology
        where p.id = :id
    """, Position.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }



    @Override
    public List<Position> findSomeActiveForCandidate() {
        Query<Position> query = getSession()
                .createQuery("""
            select distinct rp
            from Position rp
            left join fetch rp.positionTechnologies rpt
            left join fetch rpt.technology t
            where rp.status = :p_status
            order by rp.createdAt desc
        """, Position.class)
                .setParameter("p_status", PositionStatus.ENABLED);

        List<Position> fullList = query.list();

        // Trộn ngẫu nhiên
        Collections.shuffle(fullList);

        // Trả về 5 phần tử đầu tiên
        return fullList.stream().limit(5).toList();
    }

    @Override
    public List<Position> findPositionForYou() {
        Query<Position> query = getSession()
                .createQuery("""
            select distinct rp
            from Position rp
            left join fetch rp.positionTechnologies rpt
            left join fetch rpt.technology t
            where rp.status = :p_status
            order by rp.createdAt desc
        """, Position.class)
                .setParameter("p_status", PositionStatus.ENABLED);

        List<Position> fullList = query.list();

        // Trộn ngẫu nhiên
        Collections.shuffle(fullList);


        return fullList.stream().limit(9).toList();
    }


    @Override
    public List<Position> searchActiveForCandidateByKeywordAndLocation(String keyword, String city, int page, int size) {
        StringBuilder hql = new StringBuilder("""
        select distinct p from Position p
        left join fetch p.positionTechnologies pt
        where p.status = :status
    """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" and lower(p.name) like :keyword");
        }

        if (city != null && !city.trim().isEmpty()) {
            hql.append(" and p.location = :city");
        }

        hql.append(" order by p.createdAt desc");

        Query<Position> query = getSession().createQuery(hql.toString(), Position.class);
        query.setParameter("status", PositionStatus.ENABLED);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
        }

        if (city != null && !city.trim().isEmpty()) {
            query.setParameter("city", city);
        }

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.list();
    }


    @Override
    public int countAllByKeywordAndLocation(String keyword, String city) {
        StringBuilder hql = new StringBuilder("select count(p.id) from Position p where p.status = :status");

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" and lower(p.name) like :keyword");
        }

        if (city != null && !city.trim().isEmpty()) {
            hql.append(" and p.location = :city");
        }

        Query<Long> query = getSession().createQuery(hql.toString(), Long.class);
        query.setParameter("status", PositionStatus.ENABLED);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
        }

        if (city != null && !city.trim().isEmpty()) {
            query.setParameter("city", city);
        }

        Long count = query.uniqueResult();
        return count != null ? count.intValue() : 0;
    }



}
