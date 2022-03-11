package pl.wolskak.mycomputerservice.controller.forms;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.wolskak.mycomputerservice.service.UserService;


@Component
@AllArgsConstructor
public class ChangePasswordFormValidator implements Validator {

    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return ChangePasswordForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ChangePasswordForm changePasswordForm = (ChangePasswordForm) o;

        if (!userService.checkLoggedInUserPassword(changePasswordForm.getActualPassword())) { //TODO moze wywalic to do messageSource
            errors.rejectValue("actualPassword", "actual.password.not.correct", "Niepoprawne aktualne hasło.");
        } else if (!changePasswordForm.getNewPassword().equals(changePasswordForm.getRepeatNewPassword())) {
            errors.rejectValue("repeatNewPassword", "password.not.match", "Hasła muszą być takie same.");
        }
    }
}
