package pl.wolskak.mycomputerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wolskak.mycomputerservice.model.Computer;

import java.util.Optional;

public interface ComputerRepository extends JpaRepository<Computer, Integer> {
    Optional<Computer> findById(Integer id);
}
