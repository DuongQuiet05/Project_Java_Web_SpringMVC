package ra.edu.repository.application;


import ra.edu.dto.application.ApplicationFilterDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Application;
import ra.edu.entity.Technology;
import ra.edu.entity.enums.application.ApplicationProgress;
import ra.edu.entity.enums.application.CandidateConfirmStatus;
import ra.edu.entity.enums.application.InterviewResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepo {
    // todo: chung ----------------------------------------------------------------------
    Optional<Application> findById (int id);


    // todo: admin ----------------------------------------------------------------------
    List<Application> getAllApplication (int page, int size);
    int countAllApplication ();
    List<Application> filterApplications(ApplicationFilterDTO dto, int page, int size);
    int countFilteredApplications(ApplicationFilterDTO dto);
    List<Technology> findTechnologiesByPositionId(int positionId);

    //chuyển sang handling
    boolean markAsHandlingOnFirstView(int applicationId, Account adminViewer);
    // chuyển sang interview
    boolean updateInterviewSchedule(int appId, LocalDateTime interviewDateTime);

    boolean updateProgressToInterviewing(int appId);
    boolean updateInterviewUrl(int appId, String url);

    // reject
    boolean rejectApplication(int appId, String reason);

    //update result
    boolean updateApplicationResult(int appId, InterviewResult result, ApplicationProgress progress, String note);

    // todo: candidate ----------------------------------------------------------------------
    boolean updateCandidateConfirmStatus(int appId, CandidateConfirmStatus status);
    boolean insertApplication (Application application);
    List<Application> getAllApplicationByAccountId(int accountId, int page, int size);
    int countAllApplicationByAccountId(int accountId);
    boolean cancelApplication(int appId, String reason);
    Optional<Application> findLatestByCandidateAndPosition(int candidateId, int positionId);
}
