package ra.edu.dto.application;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.application.ApplicationProgress;
import ra.edu.entity.enums.application.ApplicationStatus;
import ra.edu.entity.enums.application.InterviewResult;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationListDTO {
    private int id;

    private String candidateName;
    private int candidateId;

    private String recruitmentPositionName;
    private int recruitmentPositionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ApplicationProgress progress;
    private ApplicationStatus status;
    private InterviewResult interviewResult;

}
