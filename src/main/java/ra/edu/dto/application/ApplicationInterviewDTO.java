package ra.edu.dto.application;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ra.edu.entity.enums.application.CandidateConfirmStatus;
import ra.edu.validate.application.InterviewDate;
import ra.edu.validate.application.InterviewLink;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@InterviewLink(message = "Interview link cannot be blank and must start with http/https.")
public class ApplicationInterviewDTO {
    private int id;
    private String interviewUrl;

    @NotNull(message = "Please select interview date")
    @InterviewDate(message = "Interview date must be from tomorrow onward")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate interviewDate;

    @NotNull(message = "Please select interview time")
    private LocalTime interviewTime;

    private LocalTime interviewSchedule;

    private CandidateConfirmStatus confirmStatus = CandidateConfirmStatus.NOT_CONFIRMED;
}
