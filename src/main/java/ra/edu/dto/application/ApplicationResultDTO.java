package ra.edu.dto.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.application.ApplicationProgress;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationResultDTO {
    private int id;
    @NotBlank(message = "Result note cannot be blank.")
    private String resultNote;

    private LocalDateTime interviewTime;
    private boolean canUpdateResult = false;}

