package ra.edu.dto.position.candidate;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.position.FieldType;
import ra.edu.entity.enums.position.PositionStatus;
import ra.edu.entity.enums.position.WorkForm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionDetailForCandidateDTO {
    private int id;
    private String name;
    private String description;
    private double minSalary;
    private double maxSalary;
    private int minExperience;
    private LocalDate expiredDate;
    private int numberOfVacancies;
    private String location;
    private WorkForm formOfWork;

    private List<String> techNames = new ArrayList<>();
    private String timeAgo;
}
