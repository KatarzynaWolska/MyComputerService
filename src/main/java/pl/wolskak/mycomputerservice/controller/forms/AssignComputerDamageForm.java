package pl.wolskak.mycomputerservice.controller.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignComputerDamageForm {

    private Integer id;

    @NotNull(message = "Pole nie może być puste")
    private Integer repairerId;

    public AssignComputerDamageForm(Integer id) {
        this.id = id;
    }
}
