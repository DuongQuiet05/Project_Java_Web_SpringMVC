package ra.edu.repository.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ra.edu.dto.application.ApplicationFilterDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Application;
import ra.edu.entity.Technology;
import ra.edu.entity.enums.application.ApplicationProgress;
import ra.edu.entity.enums.application.ApplicationStatus;
import ra.edu.entity.enums.application.CandidateConfirmStatus;
import ra.edu.entity.enums.application.InterviewResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class ApplicationRepoImpl implements ApplicationRepo {
    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Application> getAllApplication(int page, int size) {
        int firstResult = (page - 1) * size;
        return getSession().createQuery("""
                            from Application a
                            where a.status = :p_status
                            order by a.createdAt desc
                        """, Application.class)

                .setParameter("p_status", ApplicationStatus.ENABLED)
                .setFirstResult(firstResult)
                .setMaxResults(size)
                .list();
    }

    @Override
    public int countAllApplication() {
        Long count = getSession().createQuery("""
                            select count(a.id) from Application a
                            where a.status = :p_status
                        """, Long.class)
                .setParameter("p_status", ApplicationStatus.ENABLED)
                .uniqueResult();

        return count != null ? count.intValue() : 0;
    }


    @Override
    public List<Application> filterApplications(ApplicationFilterDTO dto, int page, int size) {
        StringBuilder base = new StringBuilder("""
                    from Application a
                    join a.candidate c
                    join a.position rp
                    where a.status = :p_status
                """);


        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            base.append(" and (lower(c.name) like :p_keyword or lower(rp.name) like :p_keyword) ");
        }

        // loc
        if (dto.getProgress() != null && !dto.getProgress().isBlank()) {
            String progress = dto.getProgress().toUpperCase();
            switch (progress) {
                case "PASSED" -> base.append(" and a.progress = 'DONE' and a.result = 'PASSED'");
                case "FAILED" -> base.append(" and a.progress = 'DONE' and a.result = 'FAILED'");
                default -> base.append(" and a.progress = :p_progress");
            }
        }



        base.append(" order by a.id desc");

        Query<Application> query = getSession().createQuery(base.toString(), Application.class);
        query.setParameter("p_status", ApplicationStatus.ENABLED);

        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            query.setParameter("p_keyword", "%" + dto.getKeyword().toLowerCase() + "%");
        }

        if (dto.getProgress() != null && !dto.getProgress().isBlank()) {
            String progress = dto.getProgress().toUpperCase();
            if (!progress.equals("PASSED") && !progress.equals("FAILED")) {
                query.setParameter("p_progress", ApplicationProgress.valueOf(progress));
            }
        }

        return query.setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .list();
    }

    @Override
    public int countFilteredApplications(ApplicationFilterDTO dto) {
        String base = """
                select count(a.id) from Application a
                join a.candidate c
                join a.position p
                where a.status = :p_status
            """;

        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            base += " and (lower(c.name) like :p_keyword or lower(p.name) like :p_keyword) ";
        }

        if (dto.getProgress() != null && !dto.getProgress().isBlank()) {
            String progress = dto.getProgress().toUpperCase();

            switch (progress) {
                case "PASSED" -> base += " and a.progress = 'DONE' and a.result = 'PASSED'";
                case "FAILED" -> base += " and a.progress = 'DONE' and a.result = 'FAILED'";
                default -> base += " and a.progress = :p_progress";
            }
        }

        Query<Long> query = getSession().createQuery(base, Long.class);
        query.setParameter("p_status", ApplicationStatus.ENABLED);

        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            query.setParameter("p_keyword", "%" + dto.getKeyword().toLowerCase() + "%");
        }

        if (dto.getProgress() != null && !dto.getProgress().isBlank()) {
            String progress = dto.getProgress().toUpperCase();
            if (!progress.equals("PASSED") && !progress.equals("FAILED")) {
                query.setParameter("p_progress", ApplicationProgress.valueOf(progress));
            }
        }

        Long count = query.uniqueResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Optional<Application> findById(int id) {
        return Optional.ofNullable(getSession().get(Application.class, id));
    }

    @Override
    public List<Technology> findTechnologiesByPositionId(int positionId) {
        String hql = "select t from PositionTechnology pt " +
                "join pt.technology t " +
                "where pt.position.id = :positionId";

        return getSession()
                .createQuery(hql, Technology.class)
                .setParameter("positionId", positionId)
                .getResultList();
    }

    @Override
    public boolean markAsHandlingOnFirstView(int applicationId, Account adminViewer) {
        String hql = "update Application a " +
                "set a.progress = :newProgress, " +
                "    a.firstViewedBy = :viewer " +
                "where a.id = :id " +
                "and a.progress = :expectedProgress " +
                "and a.firstViewedBy is null";

        int updated = getSession().createMutationQuery(hql)
                .setParameter("newProgress", ApplicationProgress.HANDLING)
                .setParameter("expectedProgress", ApplicationProgress.PENDING)
                .setParameter("viewer", adminViewer)
                .setParameter("id", applicationId)
                .executeUpdate();

        return updated > 0;
    }

    @Override
    public boolean updateInterviewSchedule(int appId, LocalDateTime interviewDateTime) {
        String hql = "update Application a set a.interviewDate = :date where a.id = :id";

        int updated = getSession().createMutationQuery(hql)
                .setParameter("date", interviewDateTime)
                .setParameter("id", appId)
                .executeUpdate();

        return updated > 0;
    }

    @Override
    public boolean updateCandidateConfirmStatus(int appId, CandidateConfirmStatus status) {
        String hql = "update Application a set a.candidateConfirmed = :status where a.id = :id";

        int updated = getSession().createMutationQuery(hql)
                .setParameter("status", status)
                .setParameter("id", appId)
                .executeUpdate();

        return updated > 0;
    }

    @Override
    public boolean updateProgressToInterviewing(int appId) {
        String hql = "update Application a set a.progress = :newProgress " +
                "where a.id = :id " +
                "and a.progress = :current " +
                "and a.candidateConfirmed = :confirmed " +
                "and a.interviewDate is not null " +
                "and a.interviewUrl is not null";

        int updated = getSession().createMutationQuery(hql)
                .setParameter("newProgress", ApplicationProgress.INTERVIEWING)
                .setParameter("current", ApplicationProgress.HANDLING)
                .setParameter("confirmed", CandidateConfirmStatus.CONFIRMED)
                .setParameter("id", appId)
                .executeUpdate();

        return updated > 0;
    }

    @Override
    public boolean updateInterviewUrl(int appId, String url) {
        String hql = "update Application a set a.interviewUrl = :url where a.id = :id";
        return getSession().createMutationQuery(hql)
                .setParameter("url", url)
                .setParameter("id", appId)
                .executeUpdate() > 0;
    }


    @Override
    public boolean rejectApplication(int appId, String reason) {
        String hql = "update Application a " +
                "set a.progress = :progress, " +
                "a.canceledReason = :reason, " +
                "a.canceledAt = :canceledAt " +
                "where a.id = :id and a.status  = :status";

        int updated = getSession().createMutationQuery(hql)
                .setParameter("progress", ApplicationProgress.REJECTED)
                .setParameter("reason", reason)
                .setParameter("canceledAt", LocalDateTime.now())
                .setParameter("id", appId)
                .setParameter("status",ApplicationStatus.ENABLED)
                .executeUpdate();

        return updated > 0;
    }

    @Override
    public boolean updateApplicationResult(int appId, InterviewResult result, ApplicationProgress progress, String note) {
        String hql = "update Application a " +
                "set a.result = :result, " +
                "a.progress = :progress, " +
                "a.resultNote = :note " +
                "where a.id = :id and a.status = :p_status";

        int updated = getSession().createMutationQuery(hql)
                .setParameter("result", result)
                .setParameter("p_status", ApplicationStatus.ENABLED)
                .setParameter("progress", progress)
                .setParameter("note", note)
                .setParameter("id", appId)
                .executeUpdate();

        return updated > 0;
    }

    @Override
    public boolean insertApplication(Application application) {
        try {
            getSession().persist(application);
            return true;
        }catch (Exception e){
            System.err.println("ERROR" + e.getMessage());
            return false;
        }
    }


    @Override
        public List<Application> getAllApplicationByAccountId(int accountId, int page, int size) {
        int firstResult = (page - 1) * size;
        return getSession().createQuery("""
                        from Application a
                        where a.status = :p_status
                        and a.candidate.id = :p_accountId
                        order by a.createdAt desc
                    """, Application.class)
                .setParameter("p_status", ApplicationStatus.ENABLED)
                .setParameter("p_accountId", accountId)
                .setFirstResult(firstResult)
                .setMaxResults(size)
                .list();
    }


    @Override
    public int countAllApplicationByAccountId(int accountId) {
        Long count = getSession().createQuery("""
                        select count(a.id) from Application a
                        where a.status = :p_status
                        and a.candidate.id = :p_accountId
                    """, Long.class)
                .setParameter("p_status", ApplicationStatus.ENABLED)
                .setParameter("p_accountId", accountId)
                .uniqueResult();

        return count != null ? count.intValue() : 0;
    }


    @Override
    public boolean cancelApplication(int appId, String reason) {
        String hql = "update Application a " +
                "set a.progress = :progress, " +
                "a.canceledReason = :reason, " +
                "a.canceledAt = :canceledAt " +
                "where a.id = :id and a.status  = :status";

        int updated = getSession().createMutationQuery(hql)
                .setParameter("progress", ApplicationProgress.CANCELED)
                .setParameter("reason", reason)
                .setParameter("canceledAt", LocalDateTime.now())
                .setParameter("id", appId)
                .setParameter("status",ApplicationStatus.ENABLED)
                .executeUpdate();

        return updated > 0;
    }

    @Override
    public Optional<Application> findLatestByCandidateAndPosition(int candidateId, int positionId) {
        return getSession().createQuery("""
            from Application a
            where a.candidate.id = :candidateId
            and a.position.id = :positionId
            order by a.createdAt desc
        """, Application.class)
                .setParameter("candidateId", candidateId)
                .setParameter("positionId", positionId)
                .setMaxResults(1)
                .uniqueResultOptional();
    }


}
