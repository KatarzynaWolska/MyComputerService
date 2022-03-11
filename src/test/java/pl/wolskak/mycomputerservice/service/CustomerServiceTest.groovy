package pl.wolskak.mycomputerservice.service

import org.springframework.boot.test.context.SpringBootTest
import pl.wolskak.mycomputerservice.model.*
import pl.wolskak.mycomputerservice.repository.RepairRepository
import spock.lang.Specification

@SpringBootTest
class CustomerServiceTest extends Specification {

    private RepairRepository repairRepository = Mock()

    private CustomerService customerService = new CustomerService(repairRepository)

    def 'findCustomersFixedRepairs should return only fixed repairs'() {
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
            computerDamage2.getStatus() >> ComputerDamageStatus.FIXED
            computerDamage1.getComputer() >> computer
            computerDamage2.getComputer() >> computer
            repair1.getComputerDamage() >> computerDamage1
            repair2.getComputerDamage() >> computerDamage2
            computer.getCustomer() >> customer
            customer.getEmail() >> email
            computer.getDamages() >> damages
            repairRepository.findAll() >> [repair1, repair2]

        when:
            def result = customerService.findCustomersFixedRepairs(email)

        then:
            result == [repair2]
    }

    def 'findCustomersFinishedRepairsToNotify should return only finished repairs with notification'() {
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
            computerDamage2.getStatus() >> ComputerDamageStatus.FIXED
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
            def result = customerService.findCustomersFinishedRepairsToNotify(email)

        then:
           result == [repair2]
    }
}
