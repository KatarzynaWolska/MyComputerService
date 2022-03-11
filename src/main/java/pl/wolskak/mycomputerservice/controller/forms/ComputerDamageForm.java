package pl.wolskak.mycomputerservice.controller.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComputerDamageForm {

    private Integer id;

    @NotNull(message = "Pole nie może być puste")
    private Integer computer;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 50, message = "Pole musi zawierać od 2 do 50 znaków")
    private String topic;

    @NotNull
    @NotEmpty(message = "Pole nie może być puste")
    @Size(min = 2, max = 1024, message = "Pole musi zawierać od 2 do 1024 znaków")
    private String description;

    private MultipartFile damagePhoto;
}
