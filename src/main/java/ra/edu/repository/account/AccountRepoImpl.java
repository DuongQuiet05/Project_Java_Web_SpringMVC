package ra.edu.repository.account;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import ra.edu.dto.candidate.ChangePasswordCandidateDTO;
import ra.edu.entity.Account;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Gender;
import ra.edu.entity.enums.account.Role;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class AccountRepoImpl implements AccountRepo {
    private final SessionFactory sessionFactory;

    private Session getSession (){
        return sessionFactory.getCurrentSession();
    }

    @Override
    public boolean insertAccount(Account account) {
        try {
            getSession().persist(account);
            return true;
        }catch (Exception e){
            System.err.println("REPO-ERROR" + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Account> findByIdAllStatus(int id) {
        return getSession().createQuery("from Account where id = :p_id",Account.class)
                .setParameter("p_id",id)
                .uniqueResultOptional();
    }

    @Override
    public Optional<Account> findByEmailAllStatus(String email) {
        return getSession().createQuery("from Account where email = :p_email",Account.class)
                .setParameter("p_email",email)
                .uniqueResultOptional();
    }

    @Override
    public Optional<Account> findByIdWithTech(int id) {
        return getSession().createQuery("""
        select distinct a from Account a
        left join fetch a.accountTechnologies at
        left join fetch at.technology
        WHERE a.id = :p_id
    """, Account.class)
                .setParameter("p_id", id)
                .uniqueResultOptional();
    }


    @Override
    public boolean updateAccount(Account account) {
        try {
            getSession().merge(account);
            return true;
        } catch (Exception e) {
            System.err.println("REPO-ERROR: " + e.getMessage());
            return false;
        }
    }


    @Override
    public boolean changePassword(ChangePasswordCandidateDTO dto) {
        MutationQuery mutationQuery  = getSession().createMutationQuery("update Account set password = :p_password where id = :p_id");
        return mutationQuery
                .setParameter("p_password",dto.getNewPassword())
                .setParameter("p_id",dto.getAccountId())
                .executeUpdate() > 0;
    }


    @Override
    public List<Account> getPaginateAccounts(int page, int size) {
        int firstResult = (page - 1) * size;

        return getSession().createQuery("from Account where role = :p_role order by createdAt desc", Account.class)
                .setParameter("p_role", Role.CANDIDATE)
                .setFirstResult(firstResult)
                .setMaxResults(size)
                .list();
    }


    @Override
    public boolean changeStatus(int accountId, AccountStatus status) {
        String hql = "update Account set status = :status where id = :id";
        return getSession().createMutationQuery(hql)
                .setParameter("status", status)
                .setParameter("id", accountId)
                .executeUpdate() > 0;
    }



    @Override
    public List<Account> findByNameKeyword(String keyword, int page, int size) {
        int firstResult = (page - 1) * size;

        return getSession().createQuery("""
            from Account
            where role = :p_role
            and lower(name) like concat('%', lower(:p_name), '%')
            order by createdAt desc
        """, Account.class)
                .setParameter("p_role", Role.CANDIDATE)
                .setParameter("p_name", keyword)
                .setFirstResult(firstResult)
                .setMaxResults(size)
                .list();
    }


    @Override
    public int countAllStatusForSearch(String keyword) {
        Long count = getSession().createQuery("""
            select count(a.id)
            from Account a
            where role = :p_role
            and lower(name) like concat('%', lower(:p_name), '%')
        """, Long.class)
                .setParameter("p_role", Role.CANDIDATE)
                .setParameter("p_name", keyword)
                .uniqueResult();

        return count != null ? count.intValue() : 0;
    }


    @Override
    public List<Account> findFilteredCandidates(String keyword, Integer techId, Gender gender, String ageRange,
                                                String expRange, int page, int size) {

        int firstResult = (page - 1) * size;
        StringBuilder hql = new StringBuilder("""
        select distinct a from Account a
        left join a.accountTechnologies ct
        where a.role = :p_role
    """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" and lower(a.name) like lower(concat('%', :p_keyword, '%')) ");
        }

        if (gender != null) {
            hql.append(" and a.gender = :p_gender ");
        }

        if (ageRange != null && ageRange.matches("\\d+-\\d+")) {
            hql.append(" and year(current_date()) - year(a.dob) between :p_ageMin and :p_ageMax ");
        }

        if (expRange != null && expRange.matches("\\d+-\\d+")) {
            hql.append(" and a.experience between :p_expMin and :p_expMax ");
        }

        if (techId != null) {
            hql.append(" and ct.technology.id = :p_techId ");
        }

        hql.append(" order by a.createdAt desc");

        System.err.println(hql);

        Query<Account> query = getSession().createQuery(hql.toString(), Account.class).setParameter("p_role", Role.CANDIDATE);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("p_keyword", keyword.trim());
        }

        if (gender != null) {
            query.setParameter("p_gender", gender);
        }

        if (ageRange != null && ageRange.matches("\\d+-\\d+")) {
            String[] parts = ageRange.split("-");
            query.setParameter("p_ageMin", Integer.parseInt(parts[0]));
            query.setParameter("p_ageMax", Integer.parseInt(parts[1]));
        }

        if (expRange != null && expRange.matches("\\d+-\\d+")) {
            String[] parts = expRange.split("-");
            query.setParameter("p_expMin", Integer.parseInt(parts[0]));
            query.setParameter("p_expMax", Integer.parseInt(parts[1]));
        }

        if (techId != null) {
            query.setParameter("p_techId", techId);
        }

        query.setFirstResult(firstResult);
        query.setMaxResults(size);
        return query.list();
    }


    @Override
    public int countFilteredCandidates(String keyword, Integer techId, Gender gender, String ageRange, String expRange) {
        StringBuilder hql = new StringBuilder("""
        select count(distinct a.id) from Account a
        left join a.accountTechnologies ct
        where a.role = :p_role
    """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" and lower(a.name) like lower(concat('%', :p_keyword, '%')) ");
        }

        if (gender != null) {
            hql.append(" and a.gender = :p_gender ");
        }

        if (ageRange != null && ageRange.matches("\\d+-\\d+")) {
            hql.append(" and year(current_date()) - year(a.dob) between :p_ageMin and :p_ageMax ");
        }

        if (expRange != null && expRange.matches("\\d+-\\d+")) {
            hql.append(" and a.experience between :p_expMin and :p_expMax ");
        }

        if (techId != null) {
            hql.append(" and ct.technology.id = :p_techId ");
        }

        var query = getSession().createQuery(hql.toString(), Long.class);
        query.setParameter("p_role", Role.CANDIDATE);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("p_keyword", keyword.trim());
        }

        if (gender != null) {
            query.setParameter("p_gender", gender);
        }

        if (ageRange != null && ageRange.matches("\\d+-\\d+")) {
            String[] parts = ageRange.split("-");
            query.setParameter("p_ageMin", Integer.parseInt(parts[0]));
            query.setParameter("p_ageMax", Integer.parseInt(parts[1]));
        }

        if (expRange != null && expRange.matches("\\d+-\\d+")) {
            String[] parts = expRange.split("-");
            query.setParameter("p_expMin", Integer.parseInt(parts[0]));
            query.setParameter("p_expMax", Integer.parseInt(parts[1]));
        }

        if (techId != null) {
            query.setParameter("p_techId", techId);
        }

        Long result = query.uniqueResult();
        return result != null ? result.intValue() : 0;
    }


    @Override
    public int countAllStatus() {
        Long count = getSession()
                .createQuery("select count(a.id) from Account a where role = :p_role", Long.class)
                .setParameter("p_role", Role.CANDIDATE)
                .uniqueResult();
        return count != null ? count.intValue() : 0;
    }



}