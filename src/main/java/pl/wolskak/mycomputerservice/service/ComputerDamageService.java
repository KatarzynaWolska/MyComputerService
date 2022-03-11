package pl.wolskak.mycomputerservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wolskak.mycomputerservice.controller.forms.ComputerDamageForm;
import pl.wolskak.mycomputerservice.model.Computer;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus;
import pl.wolskak.mycomputerservice.model.Customer;
import pl.wolskak.mycomputerservice.model.Repairer;
import pl.wolskak.mycomputerservice.model.User;
import pl.wolskak.mycomputerservice.repository.ComputerDamageRepository;
import pl.wolskak.mycomputerservice.repository.ComputerRepository;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageExistsException;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageIsAcceptedException;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageNotFoundException;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException;
import pl.wolskak.mycomputerservice.utils.FileConverter;
import pl.wolskak.mycomputerservice.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ComputerDamageService {

    private ComputerDamageRepository computerDamageRepository;
    private ComputerRepository computerRepository;
    private UserService userService;


    private void saveDamage(ComputerDamage computerDamage) throws ComputerDamageExistsException {
        if (checkIfComputerHasActiveDamage(computerDamage.getComputer().getId())) {
            throw new ComputerDamageExistsException();
        }
        computerDamageRepository.save(computerDamage);
    }

    private void updateDamage(ComputerDamage computerDamage) {
        computerDamageRepository.save(computerDamage);
    }

    public List<ComputerDamage> findUserActiveComputerDamages(String userEmail) {
        Optional<User> optionalUser = userService.findUserByEmail(userEmail);
        List<ComputerDamage> computerDamages = new ArrayList<>();

        if (optionalUser.isPresent()) {
            Customer customer = (Customer)optionalUser.get();

            customer.getComputers().forEach(computer -> {
                computerDamages.addAll(computer.getDamages().stream()
                        .filter(computerDamage -> computerDamage.getStatus() != ComputerDamageStatus.PRICED &&
                                computerDamage.getStatus() != ComputerDamageStatus.FIXED &&
                                computerDamage.getStatus() != ComputerDamageStatus.CANNOT_BE_FIXED &&
                                computerDamage.getStatus() != ComputerDamageStatus.CANCELED_BY_CUSTOMER)
                        .collect(Collectors.toList()));
            });
        }
        return computerDamages;
    }

    public ComputerDamage findComputerDamageById(Integer id) throws ComputerDamageNotFoundException, UserNotAllowedException {
        Optional<ComputerDamage> optionalDamage = computerDamageRepository.findById(id);

        if (!optionalDamage.isPresent()) {
            throw new ComputerDamageNotFoundException();
        } else {
            ComputerDamage computerDamage = optionalDamage.get();

            if (!UserUtils.checkLoggedInUserEmail(computerDamage.getComputer().getCustomer().getEmail())) {
                throw new UserNotAllowedException();
            }

            return computerDamage;
        }
    }

    boolean checkIfComputerHasActiveDamage(Integer computerId) {
        Optional<Computer> computer = computerRepository.findById(computerId);

        boolean isDamagesNull = computer.map(c -> c.getDamages() == null).orElse(false);

        if (isDamagesNull) {
            return false;
        }

        return computer.map(c -> c.getDamages().stream()
                .anyMatch(computerDamage -> computerDamage.getStatus() == ComputerDamageStatus.CANNOT_BE_FIXED ||
                        computerDamage.getStatus() == ComputerDamageStatus.ASSIGNED_TO_REPAIRER ||
                        computerDamage.getStatus() == ComputerDamageStatus.REPORTED ||
                        computerDamage.getStatus() == ComputerDamageStatus.PRICED ||
                        computerDamage.getStatus() == ComputerDamageStatus.ACCEPTED_BY_CUSTOMER))
                .orElse(false);
    }

    public void deleteComputerDamage(Integer computerDamageId, String loggedInUsername) throws ComputerDamageIsAcceptedException, UserNotAllowedException {
        computerDamageRepository.findById(computerDamageId).ifPresent(computerDamage -> {
            if (computerDamage.getStatus() != ComputerDamageStatus.REPORTED) {
                throw new ComputerDamageIsAcceptedException();
            } else if (!computerDamage.getComputer().getCustomer().getEmail().equals(loggedInUsername)) {
                throw new UserNotAllowedException();
            }
            else {
                computerDamageRepository.delete(computerDamage);
            }
        });
    }

    public void adminDeleteComputerDamage(Integer computerDamageId) {
        computerDamageRepository.findById(computerDamageId).ifPresent(computerDamage -> {
            computerDamageRepository.delete(computerDamage);
        });
    }

    public List<ComputerDamage> findComputerDamagesByStatus(ComputerDamageStatus status) {
        return computerDamageRepository.findAllByStatus(status);
    }

    public List<ComputerDamage> findComputerDamagesByStatusAndCustomer(ComputerDamageStatus status, String email) {
        return computerDamageRepository.findAllByStatus(status).stream().filter(
                computerDamage -> computerDamage.getComputer().getCustomer().getEmail().equals(email)
        ).collect(Collectors.toList());
    }

    public void assignComputerDamage(Integer damageId, Integer repairerId) throws ComputerDamageNotFoundException{
        Repairer repairer = (Repairer) userService.findUserById(repairerId);
        ComputerDamage computerDamage = computerDamageRepository.findById(damageId).orElseThrow(ComputerDamageNotFoundException::new);

        computerDamage.assignToRepairer(repairer);

        computerDamageRepository.save(computerDamage);
    }

    public void acceptComputerDamage(Integer damageId, String loggedInUsername) throws ComputerDamageNotFoundException, UserNotAllowedException {
        ComputerDamage computerDamage = computerDamageRepository.findById(damageId).orElseThrow(ComputerDamageNotFoundException::new);

        if (!computerDamage.getComputer().getCustomer().getEmail().equals(loggedInUsername)) {
            throw new UserNotAllowedException();
        }
        computerDamage.setStatus(ComputerDamageStatus.ACCEPTED_BY_CUSTOMER);
        computerDamageRepository.save(computerDamage);
    }

    public void cancelComputerDamage(Integer damageId, String loggedInUsername) {
        ComputerDamage computerDamage = computerDamageRepository.findById(damageId).orElseThrow(ComputerDamageNotFoundException::new);

        if (!computerDamage.getComputer().getCustomer().getEmail().equals(loggedInUsername)) {
            throw new UserNotAllowedException();
        }

        computerDamage.setStatus(ComputerDamageStatus.CANCELED_BY_CUSTOMER);
        computerDamage.getRepair().setShowNotification(true);
        computerDamageRepository.save(computerDamage);
    }

    public void reportComputerDamage(ComputerDamageForm computerDamageForm) {
        //Computer computer = computerService.findComputerById(computerDamageForm.getComputer());
        Computer computer = computerRepository.findById(computerDamageForm.getComputer()).orElse(null);

        ComputerDamage computerDamage = new ComputerDamage(
                computer,
                computerDamageForm.getTopic(),
                computerDamageForm.getDescription(),
                FileConverter.convertFileToByteArray(computerDamageForm.getDamagePhoto())
        );

        saveDamage(computerDamage);
    }

    public void editComputerDamage(Integer computerDamageId, ComputerDamageForm computerDamageForm) {
        //Computer computer = computerService.findComputerById(computerDamageForm.getComputer());
        Computer computer = computerRepository.findById(computerDamageForm.getComputer()).orElse(null);

        ComputerDamage computerDamage = findComputerDamageById(computerDamageId);

        computerDamage.update(
                computer,
                computerDamageForm.getTopic(),
                computerDamageForm.getDescription(),
                FileConverter.convertFileToByteArray(computerDamageForm.getDamagePhoto())
        );

        updateDamage(computerDamage);
    }
}
