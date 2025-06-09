package ra.edu.dto.candidate;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDisplayDTO {
    private int id;
    private String name;
    private String email;
    private String phone;
    private int exp;
    private Gender gender;
    private AccountStatus status;
    private LocalDate dob;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> technologies;
}
