package ra.edu.dto.application;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.application.ApplicationProgress;
import ra.edu.entity.enums.application.ApplicationStatus;
import ra.edu.entity.enums.application.CandidateConfirmStatus;
import ra.edu.entity.enums.application.InterviewResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetailDTO {
    private int id;
    private ApplicationProgress progress;

    private String cvFileName;
    private String description;

    private String positionName;
    private double salaryMin;
    private double salaryMax;
    private int experience;
    private LocalDate expiredAt;
    private LocalDateTime createdAt;
    private CandidateConfirmStatus candidateConfirmed;
    private List<TechForApplicationDTO> technologies;

    // phần này không phải hiển thị cứng, chỉ xuất hiện nếu progress = CANCELED | REJECTED
    private LocalDateTime canceledAt;
    private String canceledReason;


    // progress = INTERVIEWING
    private String interviewUrl;
    private LocalDateTime interviewDate;


    private InterviewResult result;
    //progress = DONE và result != WAITING
    private String resultNote;

}
