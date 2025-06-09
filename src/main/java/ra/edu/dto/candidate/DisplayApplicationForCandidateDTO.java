package ra.edu.dto.candidate;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.application.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DisplayApplicationForCandidateDTO {
    private int id;

    private String positionName;

    private ApplicationProgress progress;

    private ApplicationStatus status;

    private LocalDateTime appliedAt;

    private InterviewResult result;

    private String resultNote;

    private LocalDateTime interviewDate;

    private InterviewForm interviewForm;

    private String interviewUrl;

    private CandidateConfirmStatus candidateConfirmed;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String cvURL;

    private LocalDateTime canceledAt;
    private String canceledReason;

}
