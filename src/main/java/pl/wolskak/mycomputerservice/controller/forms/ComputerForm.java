package pl.wolskak.mycomputerservice.controller.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComputerForm {

    private Integer id;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 30, message = "Pole musi zawierać od 2 do 30 znaków")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Pole musi zawierać tylko litery")
    private String type;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 30, message = "Pole musi zawierać od 2 do 30 znaków")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Pole musi zawierać tylko litery")
    private String brand;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 30, message = "Pole musi zawierać od 2 do 30 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Pole musi zawierać tylko litery lub cyfry")
    private String model;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Pattern(regexp = "^\\d{4}$", message = "Nieprawidłowy format roku")
    private String yearOfProduction;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Pole musi zawierać tylko litery lub cyfry")
    private String operatingSystem;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 30, message = "Pole musi zawierać od 2 do 30 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Pole musi zawierać tylko litery lub cyfry")
    private String processor;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 30, message = "Pole musi zawierać od 2 do 30 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Pole musi zawierać tylko litery lub cyfry")
    private String graphicsCard;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 30, message = "Pole musi zawierać od 2 do 30 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Pole musi zawierać tylko litery lub cyfry")
    private String ramMemory;

    private MultipartFile photo;
}
