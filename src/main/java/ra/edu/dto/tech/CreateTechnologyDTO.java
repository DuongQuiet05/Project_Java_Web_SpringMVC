package ra.edu.dto.tech;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.validate.NotEmptyFile;
import ra.edu.validate.tech.CreateUniqueTechnologyName;

@Setter
@Getter
@NoArgsConstructor
public class CreateTechnologyDTO {
    @NotBlank(message = "Technology name is required.")
    @CreateUniqueTechnologyName(message = "Technology name already exists. Please choose a different one.")
    private String name;

    @NotBlank(message = "Technology description is required.")
    private String description;

    private String image;

    @NotEmptyFile(message = "Please upload an image.")
    private MultipartFile file;
}
