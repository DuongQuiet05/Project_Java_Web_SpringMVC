package ra.edu.repository.location;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Location;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class LocationRepoImpl implements LocationRepo {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public boolean insert(Location location) {
        try {
            getSession().persist(location);
            return true;
        } catch (Exception e) {
            System.err.println("REPO-ERROR: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Location> findAll() {
        return getSession()
                .createQuery("from Location order by id asc", Location.class)
                .list();
    }
}
