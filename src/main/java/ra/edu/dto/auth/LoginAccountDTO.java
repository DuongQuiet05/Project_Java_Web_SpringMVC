package ra.edu.dto.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LoginAccountDTO {

    @NotBlank(message = "Please enter your email address.")
    private String email;

    @NotBlank(message = "Please enter your password.")
    private String password;
}
