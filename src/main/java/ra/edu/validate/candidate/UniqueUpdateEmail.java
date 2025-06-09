package ra.edu.validate.candidate;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueUpdateEmailImpl.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUpdateEmail {
    String message() default "";
    Class<?>[] groups() default {};
    Class <? extends Payload>[] payload() default {};
}
