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
public class Customer extends User{

    @OneToMany(mappedBy="customer")
    private Set<Computer> computers;

    public Customer(String name, String surname, String email, String phoneNumber, String password, boolean active, String roles) {
        super(name, surname, email, phoneNumber, password, active, roles);
    }
}
