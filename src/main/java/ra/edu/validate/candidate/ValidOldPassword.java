package ra.edu.validate.candidate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidOldPasswordImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOldPassword {
    String message() default "Old password is incorrect.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}