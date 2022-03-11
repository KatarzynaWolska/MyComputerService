package pl.wolskak.mycomputerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wolskak.mycomputerservice.model.Repair;

import java.util.Optional;

public interface RepairRepository extends JpaRepository<Repair, Integer> {

    Optional<Repair> findById(Integer id);
}
