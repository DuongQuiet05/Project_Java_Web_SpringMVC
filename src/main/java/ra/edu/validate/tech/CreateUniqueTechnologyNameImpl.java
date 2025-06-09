package ra.edu.validate.tech;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ra.edu.entity.Technology;
import ra.edu.service.tech.TechnologyService;

import java.util.Optional;

@RequiredArgsConstructor
public class CreateUniqueTechnologyNameImpl implements ConstraintValidator<CreateUniqueTechnologyName,String> {
    private final TechnologyService technologyService;
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        } // cho @ khác xử lí

        Optional<Technology> technologyOptional = technologyService.findByNameAllStatus(value);
        return technologyOptional.isEmpty();
    }
}
