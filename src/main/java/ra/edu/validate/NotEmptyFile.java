package ra.edu.validate;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyFileImpl.class)
public @interface NotEmptyFile {
    String message() default "Please select a file.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
