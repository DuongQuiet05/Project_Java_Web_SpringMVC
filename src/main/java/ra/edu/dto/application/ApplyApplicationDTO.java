package ra.edu.dto.application;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.validate.NotEmptyFile;
import ra.edu.validate.application.PdfFile;

@Getter
@Setter
@NoArgsConstructor
public class ApplyApplicationDTO {
    private int positionId;
    private String positionName;
    private String cvURL;

    private boolean canApply; // thêm trường này vào để kiểm soát việc úng viên spam

    @NotEmptyFile(message = "Please upload an CV.")
    @PdfFile
    private MultipartFile file;
}
