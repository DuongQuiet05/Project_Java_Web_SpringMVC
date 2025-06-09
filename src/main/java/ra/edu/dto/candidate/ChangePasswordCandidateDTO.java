package ra.edu.dto.candidate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.validate.auth.PasswordFormat;
import ra.edu.validate.auth.PasswordRegisterMatches;
import ra.edu.validate.candidate.ChangePasswordMatches;
import ra.edu.validate.candidate.ValidOldPassword;

@Getter
@Setter
@NoArgsConstructor
@ChangePasswordMatches(message = "Passwords do not match")
@ValidOldPassword (message = "Old password is incorrect!")
public class ChangePasswordCandidateDTO {
    private int accountId;

    @NotBlank(message = "Please enter your old password.")
    private String oldPassword;

    @NotBlank(message = "Please enter your new password.")
    @PasswordFormat(message = "Password must be at least 8 characters long, including a letter, a number and a special character")
    private String newPassword;

    @NotBlank(message = "Please confirm your new password.")
    private String confirmNewPassword;
}

