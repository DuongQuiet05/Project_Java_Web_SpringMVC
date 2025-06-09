package ra.edu.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.validate.auth.EmailFormat;
import ra.edu.validate.auth.PasswordFormat;
import ra.edu.validate.auth.PasswordRegisterMatches;
import ra.edu.validate.auth.UniqueEmail;

@Setter
@Getter
@NoArgsConstructor
@PasswordRegisterMatches(message = "Passwords do not match")
public class RegisterAccountDTO {

    @NotBlank(message = "Please enter your name")
    private String name;

    @NotBlank(message = "Please enter your email address")
    @UniqueEmail(message = "Email already exists")
    @EmailFormat(message = "Email is not in the correct format yourmail@gmail.com")
    private String email;

    @NotBlank(message = "Please enter your password")
    @PasswordFormat(message = "Password must be at least 8 characters long, including a letter, a number and a special character")
    private String password;

    @NotBlank(message = "Please enter your confirm password")
    private String confirmPassword;
}
