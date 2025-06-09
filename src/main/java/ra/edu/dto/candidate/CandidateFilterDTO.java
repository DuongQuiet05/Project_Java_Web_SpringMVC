package ra.edu.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.account.Gender;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateFilterDTO {
    private String keyword;
    private Integer techId;
    private Gender gender;
    private String ageRange;
    private String expRange;
}
