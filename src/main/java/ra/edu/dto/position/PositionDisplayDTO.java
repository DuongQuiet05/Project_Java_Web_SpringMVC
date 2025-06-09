package ra.edu.dto.position;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.position.PositionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionDisplayDTO {
    private int id;
    private String name;
    private String description;
    private double minSalary;
    private double maxSalary;
    private int minExperience;
    private LocalDate expiredDate;
    private PositionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> technologies;
}
