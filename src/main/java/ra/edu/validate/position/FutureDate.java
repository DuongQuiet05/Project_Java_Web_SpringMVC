package ra.edu.validate.position;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureDateImpl.class)
public @interface FutureDate {
    String message() default "Date must be in the future.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
