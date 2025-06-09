package ra.edu.service.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.edu.dto.application.*;
import ra.edu.dto.candidate.DisplayApplicationForCandidateDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Application;
import ra.edu.entity.Position;
import ra.edu.entity.Technology;
import ra.edu.entity.enums.application.ApplicationProgress;
import ra.edu.entity.enums.application.ApplicationStatus;
import ra.edu.entity.enums.application.CandidateConfirmStatus;
import ra.edu.entity.enums.application.InterviewResult;
import ra.edu.repository.application.ApplicationRepo;
import ra.edu.repository.position.PositionRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService{
    private final ApplicationRepo applicationRepo;
    private final PositionRepo positionRepo;

    @Override
    public DisplayApplicationForCandidateDTO convertToDisplayDTOForCandidate(Application application) {
        if (application == null) {
            return null;
        }

        DisplayApplicationForCandidateDTO dto = new DisplayApplicationForCandidateDTO();

        dto.setId(application.getId());

        dto.setPositionName(application.getPosition() != null ? application.getPosition().getName() : "unknown");

        dto.setProgress(application.getProgress());
        dto.setStatus(application.getStatus());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setResult(application.getResult());
        dto.setResultNote(application.getResultNote());
        dto.setInterviewDate(application.getInterviewDate());
        dto.setInterviewForm(application.getInterviewForm());
        dto.setInterviewUrl(application.getInterviewUrl());
        dto.setCandidateConfirmed(application.getCandidateConfirmed());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setUpdatedAt(application.getUpdatedAt());
        dto.setCvURL(application.getCvURL());
        dto.setCanceledAt(application.getCanceledAt());
        dto.setCanceledReason(application.getCanceledReason());

        return dto;
    }

    @Override
    public Optional<Application> findById(int id) {
        return applicationRepo.findById(id);
    }


    @Override
    public boolean updateInterviewSchedule(int appId, ApplicationInterviewDTO dto) {
        // Gộp ngày và giờ thành LocalDateTime
        LocalDate date = dto.getInterviewDate();
        LocalTime time = dto.getInterviewTime();

        if (date == null || time == null) return false;

        LocalDateTime interviewDateTime = LocalDateTime.of(date, time);

        return applicationRepo.updateInterviewSchedule(appId, interviewDateTime);
    }


    @Override
    public boolean updateCandidateConfirmStatus(int appId, CandidateConfirmStatus status) {
        return applicationRepo.updateCandidateConfirmStatus(appId, status);
    }


    @Override
    public boolean updateProgressToInterviewing(int appId) {
        return applicationRepo.updateProgressToInterviewing(appId);
    }

    @Override
    public boolean updateInterviewUrl(int appId, String url) {
        return applicationRepo.updateInterviewUrl(appId,url);
    }

    @Override
    public ApplicationListDTO convertToListDTO(Application app) {
        ApplicationListDTO dto = new ApplicationListDTO();
        dto.setId(app.getId());
        dto.setCandidateId(app.getCandidate().getId());
        dto.setCandidateName(app.getCandidate().getName());
        dto.setRecruitmentPositionId(app.getPosition().getId());
        dto.setRecruitmentPositionName(app.getPosition().getName());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());
        dto.setStatus(app.getStatus());
        dto.setProgress(app.getProgress());
        dto.setInterviewResult(app.getResult());
        return dto;
    }

    @Override
    public boolean rejectApplication(int appId, ApplicationRejectDTO dto) {
        return applicationRepo.rejectApplication(appId, dto.getCanceledReason().trim());
    }


    @Override
    public boolean updateApplicationResult(int appId, String action, ApplicationResultDTO dto) {
        InterviewResult result;
        ApplicationProgress progress = ApplicationProgress.DONE;


        switch (action.toLowerCase()) {
            case "pass":
                result = InterviewResult.PASSED;
                break;
            case "fail":
                result = InterviewResult.FAILED;
                break;
            default:
                throw new IllegalArgumentException("Invalid result action: " + action);
        }

        return applicationRepo.updateApplicationResult(appId, result, progress, dto.getResultNote());
    }

    @Override
    public List<ApplicationListDTO> filterApplications(ApplicationFilterDTO dto, int page, int size) {
        Optional.ofNullable(dto.getKeyword())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .ifPresent(dto::setKeyword);

        return applicationRepo.filterApplications(dto, page, size).stream()
                .map(this::convertToListDTO)
                .toList();
    }

    @Override
    public int countFilteredApplications(ApplicationFilterDTO dto) {
        Optional.ofNullable(dto.getKeyword())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .ifPresent(dto::setKeyword);

        return applicationRepo.countFilteredApplications(dto);
    }

    @Override
    public boolean createApplication(ApplyApplicationDTO dto, Account candidate) {
        Optional<Position> posOpt = positionRepo.findById(dto.getPositionId());
        if (posOpt.isEmpty()) {
            throw new IllegalArgumentException("Cannot find position - ServiceError");
        }

        Application app = new Application();
        app.setCandidate(candidate);
        app.setPosition(posOpt.get());
        app.setCvURL(dto.getCvURL());
        app.setProgress(ApplicationProgress.PENDING);
        app.setStatus(ApplicationStatus.ENABLED);
        app.setAppliedAt(LocalDateTime.now());
        app.setCandidateConfirmed(CandidateConfirmStatus.NOT_CONFIRMED);

        return applicationRepo.insertApplication(app);
    }

    @Override
    public ApplicationDetailDTO getDetail(int applicationId, Account adminViewer) {
        // đánh dấu đang handling
        if(!applicationRepo.markAsHandlingOnFirstView(applicationId, adminViewer)){
            System.err.println("SERVICE-ERROR Không chuyển sang handing được!");
        }

        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));

        Position pos = app.getPosition();
        List<Technology> techList = applicationRepo
                .findTechnologiesByPositionId(pos.getId());

        List<TechForApplicationDTO> techDTOs = techList.stream()
                .map(t -> new TechForApplicationDTO(t.getId(), t.getName()))
                .collect(Collectors.toList());

        return new ApplicationDetailDTO(
                app.getId(),
                app.getProgress(),

                app.getCvURL(),
                app.getPosition().getDescription(),

                pos.getName(),
                pos.getMinSalary(),
                pos.getMaxSalary(),
                pos.getMinExperience(),
                pos.getExpiredDate(),
                app.getCreatedAt(),
                app.getCandidateConfirmed(),

                techDTOs,

                app.getCanceledAt(),
                app.getCanceledReason(),

                app.getInterviewUrl(),
                app.getInterviewDate(),

                app.getResult(),
                app.getResultNote()
        );

    }


    @Override
    public void checkInterviewTime(int appId , ApplicationResultDTO dto) {
        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (app.getInterviewDate() != null) {
            LocalDate interviewDay = app.getInterviewDate().toLocalDate();
            LocalDate today = LocalDate.now();

            dto.setInterviewTime(app.getInterviewDate());

            if (today.isBefore(interviewDay.plusDays(1))) {
                dto.setCanUpdateResult(false);
            } else {
                dto.setCanUpdateResult(true);
            }

        }
    }

    @Override
    public List<Application> getAllApplicationByAccountId(int accountId, int page, int size) {
        return applicationRepo.getAllApplicationByAccountId(accountId,page,size);
    }

    @Override
    public int countAllApplicationByAccountId(int accountId) {
        return applicationRepo.countAllApplicationByAccountId(accountId);
    }

    @Override
    public boolean cancelApplication(int appId, ApplicationCancelDTO dto) {
        return applicationRepo.cancelApplication(appId, dto.getCanceledReason().trim());
    }
}
