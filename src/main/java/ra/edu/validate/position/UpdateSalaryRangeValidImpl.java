package ra.edu.validate.position;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ra.edu.dto.position.UpdatePositionDTO;

public class UpdateSalaryRangeValidImpl implements ConstraintValidator<UpdateSalaryRangeValid, UpdatePositionDTO> {

    @Override
    public boolean isValid(UpdatePositionDTO dto, ConstraintValidatorContext context) {
        double max = dto.getMaxSalary();
        double min = dto.getMinSalary();
        if (max == 0) {
            return true;
        }

        if (max < min) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("maxSalary")
                    .addConstraintViolation();
            return false;
        }


        return true;
    }
}
