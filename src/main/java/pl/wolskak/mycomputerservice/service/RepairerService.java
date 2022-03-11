package pl.wolskak.mycomputerservice.service;

import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus;
import pl.wolskak.mycomputerservice.model.Repair;
import pl.wolskak.mycomputerservice.model.Repairer;
import pl.wolskak.mycomputerservice.repository.ComputerDamageRepository;
import pl.wolskak.mycomputerservice.repository.RepairRepository;
import pl.wolskak.mycomputerservice.repository.UserRepository;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageNotFoundException;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException;
import pl.wolskak.mycomputerservice.utils.InvoiceBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class RepairerService {

    private ComputerDamageRepository computerDamageRepository;
    private UserRepository userRepository;
    private RepairRepository repairRepository;

    public List<ComputerDamage> getDamagesAssignedToRepairer(String repairerEmail) {
        Repairer repairer = (Repairer) userRepository.findByEmail(repairerEmail).orElse(null);

        return computerDamageRepository.findAllByRepairerAndStatus(repairer, ComputerDamageStatus.ASSIGNED_TO_REPAIRER);
    }

    public List<ComputerDamage> getPendingDamagesAssignedToRepairer(String repairerEmail) {
        Repairer repairer = (Repairer) userRepository.findByEmail(repairerEmail).orElse(null);

        return computerDamageRepository.findAllByRepairerAndStatus(repairer, ComputerDamageStatus.PRICED);
    }

    public List<ComputerDamage> getAcceptedDamagesAssignedToRepairer(String repairerEmail) {
        Repairer repairer = (Repairer) userRepository.findByEmail(repairerEmail).orElse(null);

        return computerDamageRepository.findAllByRepairerAndStatus(repairer, ComputerDamageStatus.ACCEPTED_BY_CUSTOMER);
    }

    public void addRemarksToComputerDamage(Integer damageId, String remarks, String price) {
        computerDamageRepository.findById(damageId).ifPresent(computerDamage -> {
            Repair repair = new Repair(computerDamage, remarks, price);
            computerDamage.setRepair(repair);
            computerDamage.setStatus(ComputerDamageStatus.PRICED);
            repairRepository.save(repair);
            computerDamageRepository.save(computerDamage);
        });
    }

    public void reportFixOfComputerDamage(Integer damageId) {
        computerDamageRepository.findById(damageId).ifPresent(computerDamage -> {
            computerDamage.setStatus(ComputerDamageStatus.FIXED);
            computerDamage.getRepair().setShowNotification(true);
            computerDamageRepository.save(computerDamage);
        });
    }

    public void reportImpossibleFixOfComputerDamage(Integer damageId) {
        computerDamageRepository.findById(damageId).ifPresent(computerDamage -> {
            computerDamage.setStatus(ComputerDamageStatus.CANNOT_BE_FIXED);
            computerDamage.getRepair().setShowNotification(true);
            computerDamageRepository.save(computerDamage);
        });
    }

    public void createInvoice(Integer damageId) {
        InvoiceBuilder invoiceBuilder = new InvoiceBuilder();
        computerDamageRepository.findById(damageId).ifPresent(computerDamage -> {
            try {
                byte[] invoice = invoiceBuilder.createInvoice(computerDamage);
                saveInvoice(invoice, computerDamage.getRepair());
            } catch (IOException | DocumentException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    public ComputerDamage findComputerDamageById(Integer id) throws ComputerDamageNotFoundException, UserNotAllowedException {
        return computerDamageRepository.findById(id).orElseThrow(ComputerDamageNotFoundException::new);
    }

    public List<Repair> findRepairsCancelledByCustomer(String loggedInUsername) {
        return repairRepository.findAll().stream()
                .filter(repair -> repair.getComputerDamage().getRepairer().getEmail().equals(loggedInUsername))
                .filter(repair -> repair.getComputerDamage().getStatus() == ComputerDamageStatus.CANCELED_BY_CUSTOMER)
                .filter(repair -> repair.getShowNotification() != null && repair.getShowNotification())
                .collect(Collectors.toList());
    }

    public void confirmNotification(Integer repairId) {
        repairRepository.findById(repairId).ifPresent(repair -> {
            repair.setShowNotification(false);
            repairRepository.save(repair);
        });
    }

    private void saveInvoice(byte[] invoice, Repair repair) {
        repair.setInvoice(invoice);
        repairRepository.save(repair);
    }
}
