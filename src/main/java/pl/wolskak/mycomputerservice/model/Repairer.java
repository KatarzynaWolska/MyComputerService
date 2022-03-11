package pl.wolskak.mycomputerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Repairer extends User {

    @OneToMany(mappedBy="repairer")
    private Set<ComputerDamage> computerDamages;

    public Repairer(String name, String surname, String email, String phoneNumber, String password) {
        super(name, surname, email, phoneNumber, password, true, "ROLE_REPAIRER");
    }

    public Repairer(Integer id, String name, String surname, String email, String phoneNumber, String password) {
        super(id, name, surname, email, phoneNumber, password, true, "ROLE_REPAIRER");
    }
}
