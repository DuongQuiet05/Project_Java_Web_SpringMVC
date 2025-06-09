package ra.edu.validate.auth;

import jakarta.validation.ConstraintValidatorContext;

//public class PasswordForgotMatchesImpl implements ConstraintValidator<PasswordForgotMatches, ChangePasswordForgotDTO> {
//    @Override
//    public boolean isValid(ChangePasswordForgotDTO dto, ConstraintValidatorContext context) {
//        if (dto.getNewPassword() == null || dto.getConfirmNewPassword() == null || dto.getConfirmNewPassword().isBlank()) {
//            return true;
//        }
//
//        boolean match = dto.getNewPassword().equals(dto.getConfirmNewPassword());
//        if (!match) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
//                    .addPropertyNode("confirmNewPassword")
//                    .addConstraintViolation();
//        }
//
//        return match;
//    }
//}
