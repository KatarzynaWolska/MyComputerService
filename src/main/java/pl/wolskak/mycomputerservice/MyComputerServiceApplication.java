package pl.wolskak.mycomputerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.wolskak.mycomputerservice.repository.ComputerDamageRepository;
import pl.wolskak.mycomputerservice.repository.ComputerRepository;
import pl.wolskak.mycomputerservice.repository.RepairRepository;
import pl.wolskak.mycomputerservice.repository.UserRepository;
import pl.wolskak.mycomputerservice.repository.VerificationTokenRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = {UserRepository.class, VerificationTokenRepository.class, ComputerRepository.class, ComputerDamageRepository.class, RepairRepository.class})
public class MyComputerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyComputerServiceApplication.class, args);
    }

}
