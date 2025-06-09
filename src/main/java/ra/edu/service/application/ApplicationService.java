package ra.edu.service.application;

import ra.edu.dto.application.*;
import ra.edu.dto.candidate.DisplayApplicationForCandidateDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Application;
import ra.edu.entity.enums.application.CandidateConfirmStatus;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    // todo: convert -------------------------------------------------------------------------------
    DisplayApplicationForCandidateDTO convertToDisplayDTOForCandidate(Application application);
    ApplicationListDTO convertToListDTO(Application app);

    // todo: admin --------------------------------------------------------------------
    List<ApplicationListDTO> filterApplications(ApplicationFilterDTO dto, int page, int size);
    int countFilteredApplications(ApplicationFilterDTO dto);
    Optional<Application> findById (int id);
    ApplicationDetailDTO getDetail(int applicationId, Account adminViewer);

    // chuyển sang interview
    boolean updateInterviewSchedule(int appId, ApplicationInterviewDTO dto);
    ;
    boolean updateInterviewUrl(int appId, String url);

    boolean updateProgressToInterviewing(int appId);
    boolean rejectApplication(int appId, ApplicationRejectDTO dto);
    boolean updateApplicationResult(int appId, String action, ApplicationResultDTO dto);

    void checkInterviewTime (int appId,ApplicationResultDTO dto);

    //todo: candidate ----------------------------------------------------------------------------
    boolean createApplication(ApplyApplicationDTO dto, Account candidate);
    boolean updateCandidateConfirmStatus(int appId, CandidateConfirmStatus status);
    List<Application> getAllApplicationByAccountId(int accountId, int page, int size);
    int countAllApplicationByAccountId(int accountId);
    boolean cancelApplication(int appId, ApplicationCancelDTO dto);
}
