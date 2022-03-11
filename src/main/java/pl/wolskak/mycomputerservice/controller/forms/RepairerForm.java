package pl.wolskak.mycomputerservice.controller.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RepairerForm {

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 30, message = "Pole musi zawierać od 2 do 30 znaków")
    private String name;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 30, message = "Pole musi zawierać od 2 do 30 znaków")
    private String surname;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    private String email;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Pattern(regexp = "^\\d{9}$", message = "Numer telefonu musi składać się z dziewięciu cyfr")
    private String phoneNumber;
}
