package ra.edu.dto.candidate;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.entity.enums.account.Gender;
import ra.edu.validate.auth.EmailFormat;
import ra.edu.validate.candidate.UniqueUpdateEmail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@UniqueUpdateEmail(message = "Email already exist!")
public class UpdateCandidateProfileDTO {
    private int id;

    @NotBlank(message = "Please enter your name")
    private String name;

    @NotBlank(message = "Please enter your email")
    @EmailFormat(message = "Email is not in the correct format yourmail@gmail.com")
    private String email;

    @Min(value = 0, message = "Experience must be positive")
    private int experience;

    @NotNull(message = "Please enter your gender")
    private Gender gender;

    @NotNull(message = "Please enter your date of birth")
    private LocalDate dob;

    @NotBlank(message = "Please enter your description")
    private String description;

    private String avatar;


    private MultipartFile file;

    @NotEmpty(message = "Please select at least one technology.")
    private List<Integer> technologyIds = new ArrayList<>();
}
