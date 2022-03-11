package pl.wolskak.mycomputerservice.controller.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordForm {

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    private String actualPassword;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
            message = "Hasło musi zawierać minimum 8 znaków, w tym małą literę, dużą literę, cyfrę i znak specjalny")
    private String newPassword;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    private String repeatNewPassword;
}
