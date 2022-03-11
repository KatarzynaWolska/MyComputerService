package pl.wolskak.mycomputerservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus;
import pl.wolskak.mycomputerservice.model.Repair;
import pl.wolskak.mycomputerservice.repository.RepairRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerService {

    private RepairRepository repairRepository;

    public List<Repair> findCustomersFixedRepairs(String loggedInUsername) {
        return repairRepository.findAll().stream()
                .filter(repair -> repair.getComputerDamage().getComputer().getCustomer().getEmail().equals(loggedInUsername))
                .filter(repair -> repair.getComputerDamage().getStatus() == ComputerDamageStatus.FIXED)
                .collect(Collectors.toList());
    }

    public byte[] findRepairInvoice(Integer repairId) {
        return repairRepository.findById(repairId).map(Repair::getInvoice).orElse(null);
    }

    public void confirmRepairNotification(Integer repairId) {
        repairRepository.findById(repairId).ifPresent(repair -> {
            repair.setShowNotification(false);
            repairRepository.save(repair);
        });
    }

    public List<Repair> findCustomersFinishedRepairsToNotify(String loggedInUsername) {
        return repairRepository.findAll().stream()
                .filter(repair -> repair.getComputerDamage().getComputer().getCustomer().getEmail().equals(loggedInUsername))
                .filter(repair -> repair.getComputerDamage().getStatus() == ComputerDamageStatus.FIXED ||
                        repair.getComputerDamage().getStatus() == ComputerDamageStatus.CANNOT_BE_FIXED)
                .filter(repair -> repair.getShowNotification() != null && repair.getShowNotification())
                .collect(Collectors.toList());
    }
}
