package pl.wolskak.mycomputerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wolskak.mycomputerservice.model.User;
import pl.wolskak.mycomputerservice.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
