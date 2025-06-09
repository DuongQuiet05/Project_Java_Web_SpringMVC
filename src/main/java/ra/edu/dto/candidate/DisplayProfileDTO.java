package ra.edu.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.Application;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DisplayProfileDTO {
    private int id;
    private String name;
    private String email;
    private String phone;

    private int experience;
    private Gender gender;

    private AccountStatus status;

    private String description;
    private String avatar;
    private LocalDate dob;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> technologies;
}
