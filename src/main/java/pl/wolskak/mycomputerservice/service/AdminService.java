package pl.wolskak.mycomputerservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wolskak.mycomputerservice.controller.forms.RepairerForm;
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus;
import pl.wolskak.mycomputerservice.model.Customer;
import pl.wolskak.mycomputerservice.model.Repairer;
import pl.wolskak.mycomputerservice.repository.UserRepository;
import pl.wolskak.mycomputerservice.service.exceptions.RepairerHasActiveDamageException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {

    private UserRepository userRepository;
    private UserService userService;

    private final String DEFAULT_PASSWORD = "MyServ1!";


    public void deleteRepairer(Integer userId) throws RepairerHasActiveDamageException {
        userRepository.findById(userId)
                .map(user -> (Repairer) user)
                .ifPresent(repairer -> {
                    if (!repairer.getComputerDamages().isEmpty()) {
                        repairer.getComputerDamages().stream()
                                .filter(computerDamage -> (computerDamage.getStatus() != ComputerDamageStatus.REPORTED) ||
                                        (computerDamage.getStatus() != ComputerDamageStatus.FIXED) ||
                                        (computerDamage.getStatus() != ComputerDamageStatus.CANNOT_BE_FIXED))
                                .findAny()
                                .ifPresent(computerDamage -> {
                            throw new RepairerHasActiveDamageException();
                        });

                    } else {
                        userRepository.delete(repairer);
                    }
                });
    }

    public void deleteUser(Integer userId) {
        userRepository.findById(userId).ifPresent(user -> userRepository.delete(user));
    }

    public void createRepairerAccount(RepairerForm repairerForm) {
        Repairer repairer = new Repairer(
                repairerForm.getName(),
                repairerForm.getSurname(),
                repairerForm.getEmail(),
                repairerForm.getPhoneNumber(),
                userService.encodePassword(DEFAULT_PASSWORD)
        );

        userService.createUserAccount(repairer);
    }

    public List<Customer> findAllCustomers() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Customer)
                .map(user -> (Customer) user)
                .collect(Collectors.toList());
    }

    public List<Repairer> findAllRepairers() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Repairer)
                .map(user -> (Repairer) user)
                .collect(Collectors.toList());
    }
}
