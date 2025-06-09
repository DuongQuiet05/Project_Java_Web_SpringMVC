package ra.edu.validate.application;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = PdfFileImpl.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PdfFile {
    String message() default "File must be in PDF format.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
