package pl.wolskak.mycomputerservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wolskak.mycomputerservice.controller.forms.ComputerForm;
import pl.wolskak.mycomputerservice.model.Computer;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus;
import pl.wolskak.mycomputerservice.model.Customer;
import pl.wolskak.mycomputerservice.model.User;
import pl.wolskak.mycomputerservice.repository.ComputerRepository;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageExistsException;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerNotFoundException;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException;
import pl.wolskak.mycomputerservice.utils.FileConverter;
import pl.wolskak.mycomputerservice.utils.UserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ComputerService {

    private ComputerRepository computerRepository;
    private UserService userService;
    private ComputerDamageService computerDamageService;

    public Computer findComputerById(Integer id) throws ComputerNotFoundException, UserNotAllowedException {
        Optional<Computer> optionalComputer = computerRepository.findById(id);

        if (!optionalComputer.isPresent()) {
            throw new ComputerNotFoundException(id);
        } else {
            Computer computer = optionalComputer.get();

            if (!UserUtils.checkLoggedInUserEmail(computer.getCustomer().getEmail())) {
                throw new UserNotAllowedException();
            }

            return computer;
        }
    }

    public List<Computer> findUserComputers(String userEmail) {
        Optional<User> optionalUser = userService.findUserByEmail(userEmail);
        return optionalUser.map(user -> new ArrayList<>(((Customer) user).getComputers())).orElse(new ArrayList<>());
    }

    public void deleteComputer(Integer computerId) throws ComputerDamageExistsException {
        if (computerDamageService.checkIfComputerHasActiveDamage(computerId)) {
            throw new ComputerDamageExistsException();
        }

        computerRepository.findById(computerId).ifPresent(
                computer -> computerRepository.delete(computer)
        );
    }

    public List<ComputerDamage> findHistoryOfComputerDamages(Integer computerId) {
        return computerRepository.findById(computerId)
                .map(computer -> computer.getDamages().stream().filter(computerDamage ->
                                computerDamage.getStatus() == ComputerDamageStatus.FIXED ||
                                computerDamage.getStatus() == ComputerDamageStatus.CANNOT_BE_FIXED ||
                                computerDamage.getStatus() == ComputerDamageStatus.CANCELED_BY_CUSTOMER)
                        .collect(Collectors.toList())
                ).orElse(Collections.emptyList());
    }

    public void createComputer(ComputerForm computerForm, String loggedInUsername) {
        User loggedInUser = userService.findUserByEmail(loggedInUsername).orElse(null);

        if (loggedInUser instanceof Customer) {
            Computer newComputer = new Computer(
                    computerForm.getType(),
                    computerForm.getBrand(),
                    computerForm.getModel(),
                    computerForm.getYearOfProduction(),
                    computerForm.getOperatingSystem(),
                    computerForm.getProcessor(),
                    computerForm.getGraphicsCard(),
                    computerForm.getRamMemory(),
                    (Customer) loggedInUser,
                    FileConverter.convertFileToByteArray(computerForm.getPhoto()));

            saveComputer(newComputer);
        }
    }

    public void updateComputer(Integer computerId, ComputerForm computerForm, String loggedInUsername) {
        User loggedInUser = userService.findUserByEmail(loggedInUsername).orElse(null);

        if (loggedInUser instanceof Customer) {
            Computer computer = findComputerById(computerId);
            computer.update(
                    computerForm.getType(),
                    computerForm.getBrand(),
                    computerForm.getModel(),
                    computerForm.getYearOfProduction(),
                    computerForm.getOperatingSystem(),
                    computerForm.getProcessor(),
                    computerForm.getGraphicsCard(),
                    computerForm.getRamMemory(),
                    (Customer) loggedInUser,
                    FileConverter.convertFileToByteArray(computerForm.getPhoto())
            );

            saveComputer(computer);
        }
    }

    private void saveComputer(Computer computer) {
        computerRepository.save(computer);
    }
}
