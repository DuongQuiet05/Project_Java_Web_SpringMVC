package ra.edu.repository.account;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ra.edu.entity.weeks.AccountTechnology;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class AccountTechnologyRepoImpl implements AccountTechnologyRepo {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<AccountTechnology> findByAccountIds(List<Integer> accountIds) {
        return getSession().createQuery(
                        "FROM AccountTechnology at " +
                                "JOIN FETCH at.technology " +
                                "WHERE at.account.id IN (:ids)", AccountTechnology.class)
                .setParameter("ids", accountIds)
                .list();
    }

    @Override
    public boolean deleteByAccountId(int accountId) {
        try {
            getSession().createMutationQuery("""
            delete from AccountTechnology 
            where account.id = :accountId
        """)
                    .setParameter("accountId", accountId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("REPO-ERROR: Failed to delete tech links for account " + accountId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean insertAll(List<AccountTechnology> list) {
        try {
            for (AccountTechnology techLink : list) {
                getSession().merge(techLink);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Insert candidate tech links failed: " + e.getMessage());
            return false;
        }
    }


}
