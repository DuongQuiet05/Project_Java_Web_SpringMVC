package ra.edu.validate.position;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class MaxSizeTechnologyImpl implements ConstraintValidator<MaxSizeTechnology, List<Integer>> {

    @Override
    public boolean isValid(List<Integer> technologyIds, ConstraintValidatorContext context) {
        if (technologyIds == null) return true;
        return technologyIds.size() <= 5;
    }
}
