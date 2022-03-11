package pl.wolskak.mycomputerservice.controller.forms;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Component
public class RegistrationFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return RegistrationForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        RegistrationForm registrationForm = (RegistrationForm) o;

        if (!registrationForm.getPassword().equals(registrationForm.getRepeatPassword())) {
            errors.rejectValue("repeatPassword", "password.not.match", "Hasła muszą być takie same.");
        }

        try {
            InternetAddress email = new InternetAddress(registrationForm.getEmail());
            email.validate();
        } catch (AddressException ex) {
            errors.rejectValue("email", "email.not.correct", "Niepoprawny format e-mail.");
        }
    }
}
