package ra.edu.validate.application;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ra.edu.dto.application.ApplicationInterviewDTO;
import ra.edu.entity.enums.application.CandidateConfirmStatus;

public class InterviewLinkImpl implements ConstraintValidator<InterviewLink, ApplicationInterviewDTO>
{
    @Override
    public boolean isValid(ApplicationInterviewDTO dto, ConstraintValidatorContext context) {
        if (dto.getConfirmStatus() == CandidateConfirmStatus.NOT_CONFIRMED) {
            return true;
        }

        String url = dto.getInterviewUrl();

        boolean valid = url != null
                && !url.isBlank()
                && (url.startsWith("http://") || url.startsWith("https://"));

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("interviewUrl") // báo lỗi đúng field
                    .addConstraintViolation();
        }
        return valid;
    }
}
