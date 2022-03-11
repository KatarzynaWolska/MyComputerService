package pl.wolskak.mycomputerservice.service

import org.springframework.boot.test.context.SpringBootTest
import pl.wolskak.mycomputerservice.model.*
import pl.wolskak.mycomputerservice.repository.ComputerDamageRepository
import pl.wolskak.mycomputerservice.repository.RepairRepository
import pl.wolskak.mycomputerservice.repository.UserRepository
import spock.lang.Specification

@SpringBootTest
class RepairerServiceTest extends Specification {

    private ComputerDamageRepository computerDamageRepository = Mock()
    private UserRepository userRepository = Mock()
    private RepairRepository repairRepository = Mock()

    private RepairerService repairerService = new RepairerService(computerDamageRepository, userRepository, repairRepository)

    def 'findRepairsCancelledByCustomer should return only cancelled repairs with notification'() {
        given:
            def email = "mail"
            def customer = Mock(Customer)
            def repair1 = Mock(Repair)
            def repair2 = Mock(Repair)
            def computerDamage1 = Mock(ComputerDamage)
            def computerDamage2 = Mock(ComputerDamage)
            def damages = [computerDamage1, computerDamage2]
            def computer = Mock(Computer)

        and:
            computerDamage1.getStatus() >> ComputerDamageStatus.ASSIGNED_TO_REPAIRER
            computerDamage2.getStatus() >> ComputerDamageStatus.CANCELED_BY_CUSTOMER
            computerDamage1.getComputer() >> computer
            computerDamage2.getComputer() >> computer
            repair1.getComputerDamage() >> computerDamage1
            repair2.getComputerDamage() >> computerDamage2
            repair2.getShowNotification() >> true
            computer.getCustomer() >> customer
            customer.getEmail() >> email
            computer.getDamages() >> damages
            repairRepository.findAll() >> [repair1, repair2]

        when:
            def result = repairerService.findRepairsCancelledByCustomer(email)

        then:
            result == [repair2]
    }
}
