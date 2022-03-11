package pl.wolskak.mycomputerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus;
import pl.wolskak.mycomputerservice.model.Repairer;

import java.util.List;
import java.util.Optional;


public interface ComputerDamageRepository extends JpaRepository<ComputerDamage, Integer> {
    Optional<ComputerDamage> findById(Integer id);

    List<ComputerDamage> findAllByStatus(ComputerDamageStatus status);

    List<ComputerDamage> findAllByRepairerAndStatus(Repairer repairer, ComputerDamageStatus status);
}
