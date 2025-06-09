package ra.edu.dto.position;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.enums.position.FieldType;
import ra.edu.entity.enums.position.WorkForm;
import ra.edu.validate.position.FutureDate;
import ra.edu.validate.position.MaxSizeTechnology;
import ra.edu.validate.position.CreateSalaryRangeValid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@CreateSalaryRangeValid(message = "Maximum salary must be greater than minimum salary.")
public class CreatePositionDTO {
    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Description is required.")
    private String description;

    @PositiveOrZero(message = "Minimum salary must be >= 0.")
    private double minSalary;

    @Positive(message = "Maximum salary must be > 0")
    private double maxSalary;

    @Min(value = 0, message = "Experience must be >= 0.")
    private int minExperience;

    @NotNull(message = "Number of vacancies is required.")
    @Min(value = 1, message = "Number of vacancies must be at least 1.")
    private Integer numberOfVacancies;

    @NotNull(message = "Field type is required.")
    private FieldType field = FieldType.IT;

    @NotBlank(message = "Location is required.")
    private String location;

    @NotNull(message = "Form of work is required.")
    private WorkForm formOfWork;

    @NotNull(message = "Expiration date is required.")
    @FutureDate(message = "Expiration date must be at least tomorrow.")
    private LocalDate expiredDate;

    @NotEmpty(message = "Please select at least one technology.")
    @MaxSizeTechnology(message = "You can select up to 5 technologies only.")
    private List<Integer> technologyIds = new ArrayList<>();

}
