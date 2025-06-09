package ra.edu.validate.tech;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ra.edu.dto.tech.UpdateTechnologyDTO;
import ra.edu.entity.Technology;
import ra.edu.service.tech.TechnologyService;

import java.util.Optional;

@RequiredArgsConstructor
public class UpdateUniqueTechnologyNameImpl implements ConstraintValidator<UpdateUniqueTechnologyName, UpdateTechnologyDTO> {

    private final TechnologyService technologyService;

    @Override
    public boolean isValid(UpdateTechnologyDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            return true; // để validator khác xử lý null/blank
        }

        Optional<Technology> optional = technologyService.findByNameAllStatus(dto.getName());

        if (optional.isEmpty()) return true;

        Technology existing = optional.get();

        // xử lý hiển thị lỗi nếu dùng type thay cho fiel
        if (existing.getId() != dto.getId()) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("name")
                    .addConstraintViolation();
            return false;
        }


        return true;
    }
}