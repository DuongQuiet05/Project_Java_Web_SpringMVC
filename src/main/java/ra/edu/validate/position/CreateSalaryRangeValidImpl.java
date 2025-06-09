package ra.edu.validate.position;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ra.edu.dto.position.CreatePositionDTO;


public class CreateSalaryRangeValidImpl implements ConstraintValidator<CreateSalaryRangeValid, CreatePositionDTO> {

    @Override
    public boolean isValid(CreatePositionDTO dto, ConstraintValidatorContext context) {
        double max = dto.getMaxSalary();
        double min = dto.getMinSalary();
        if (max == 0) {
            return true; // để validator khác xử lý null/blank
        }

        // xử lý hiển thị lỗi nếu dùng type thay cho fiel
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
