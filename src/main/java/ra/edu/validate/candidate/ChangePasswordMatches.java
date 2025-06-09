package ra.edu.validate.candidate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ra.edu.validate.auth.PasswordRegisterMatchesImpl;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChangePasswordMatchesImpl.class)
public @interface ChangePasswordMatches {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
