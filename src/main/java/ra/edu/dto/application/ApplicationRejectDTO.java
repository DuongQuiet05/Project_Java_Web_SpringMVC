package ra.edu.dto.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationRejectDTO {
    @NotBlank(message = "Reason cannot be blank.")
    private String canceledReason;
}
