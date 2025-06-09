package ra.edu.repository.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ra.edu.entity.auth.RememberToken;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class RememberTokenRepoImpl implements RememberTokenRepo {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public boolean insert(RememberToken token) {
        try {
            getSession().persist(token);
            return true;
        }catch (Exception e){
            System.err.println("REPO-ERROR" + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<RememberToken> findByToken(String token) {
        return getSession()
                .createQuery("from RememberToken where token = :token", RememberToken.class)
                .setParameter("token", token)
                .uniqueResultOptional();
    }

    @Override
    public boolean deleteByToken(String token) {
       return getSession()
                .createMutationQuery("delete from RememberToken where token = :token")
                .setParameter("token", token)
                .executeUpdate() > 0;
    }
}
