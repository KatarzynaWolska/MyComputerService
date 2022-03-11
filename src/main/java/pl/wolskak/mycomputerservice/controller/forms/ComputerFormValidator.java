package pl.wolskak.mycomputerservice.controller.forms;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.wolskak.mycomputerservice.model.Computer;

import java.util.Calendar;

@Component
public class ComputerFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return ComputerForm.class.equals(aClass) || Computer.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            ComputerForm computerForm = (ComputerForm) o;

            int actualYear = Calendar.getInstance().get(Calendar.YEAR);
            int formComputerProductionYear = Integer.parseInt(computerForm.getYearOfProduction());

            if (formComputerProductionYear > actualYear) {
                errors.rejectValue("yearOfProduction", "year.greater.than.actual", "Rok produkcji musi być mniejszy od aktualnego");
            }
        } catch (NumberFormatException ex) {
            errors.rejectValue("yearOfProduction", "year.value.is.empty", "Pole nie może być puste");
        }
    }
}
