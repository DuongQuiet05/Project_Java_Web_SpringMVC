package ra.edu.dto.tech;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.validate.tech.UpdateUniqueTechnologyName;

@Setter
@Getter
@NoArgsConstructor
@UpdateUniqueTechnologyName(message = "Technology name already exists. Please enter a different name.")
public class UpdateTechnologyDTO {
    private int id;

    @NotBlank(message = "Technology name is required.")
    private String name;

    @NotBlank(message = "Technology description is required.")
    private String description;

    private String image;

    private MultipartFile file;
}
