package ra.edu.validate.tech;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CreateUniqueTechnologyNameImpl.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateUniqueTechnologyName {
    String message() default "";
    Class<?>[] groups() default {};
    Class <? extends Payload>[] payload() default {};
}

