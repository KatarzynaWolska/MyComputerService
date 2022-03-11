package pl.wolskak.mycomputerservice.service

import org.springframework.boot.test.context.SpringBootTest
import pl.wolskak.mycomputerservice.model.Computer
import pl.wolskak.mycomputerservice.model.ComputerDamage
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus
import pl.wolskak.mycomputerservice.model.Customer
import pl.wolskak.mycomputerservice.repository.ComputerDamageRepository
import pl.wolskak.mycomputerservice.repository.ComputerRepository
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageIsAcceptedException
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageNotFoundException
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException
import spock.lang.Specification


@SpringBootTest
class ComputerDamageServiceTest extends Specification {

    private ComputerDamageRepository computerDamageRepository = Mock()
    private ComputerRepository computerRepository = Mock()
    private UserService userService = Mock()

    private ComputerDamageService computerDamageService = new ComputerDamageService(computerDamageRepository, computerRepository, userService)

    def 'findComputerDamageById should throw ComputerDamageNotFoundException when computerDamage with id is not found'() {
        given:
            def id = 200
            computerDamageRepository.findById(*_) >> Optional.ofNullable(null)

        when:
            computerDamageService.findComputerDamageById(id)

        then:
            thrown(ComputerDamageNotFoundException)
    }

    def 'checkIfComputerHasActiveDamage should return true if computer has active damage'() {
        given:
            def id = 200
            def computer = Mock(Computer)
            def computerDamage = Mock(ComputerDamage)
            computerDamage.getStatus() >> ComputerDamageStatus.ASSIGNED_TO_REPAIRER
            computer.getDamages() >> [computerDamage]
            computerRepository.findById(id) >> Optional.of(computer)

        when:
            def result = computerDamageService.checkIfComputerHasActiveDamage(id)

        then:
            result
    }

    def 'checkIfComputerHasActiveDamage should return false if computer has active damage'() {
        given:
            def id = 200
            def computer = Mock(Computer)
            def computerDamage = Mock(ComputerDamage)
            computerDamage.getStatus() >> ComputerDamageStatus.FIXED
            computer.getDamages() >> [computerDamage]
            computerRepository.findById(id) >> Optional.of(computer)

        when:
            def result = computerDamageService.checkIfComputerHasActiveDamage(id)

        then:
            !result
    }

    def "deleteComputerDamage should delete computer damage with specified id"() {
        given:
            def id = 300
            def customer = Mock(Customer)
            def computer = Mock(Computer)
            def email = "mail"

            computer.getCustomer() >> customer
            customer.getEmail() >> email

            def computerDamage = new ComputerDamage(id, computer, "temat", "opis", null, ComputerDamageStatus.REPORTED, null, null)

        and:
            computerDamageRepository.findById(id) >> Optional.of(computerDamage)

        when:
            computerDamageService.deleteComputerDamage(id, email)

        then:
            1 * computerDamageRepository.delete(computerDamage)
    }

    def "deleteComputerDamage should throw ComputerDamageIsReportedException when it is accepted"() {
        given:
            def id = 300
            def customer = Mock(Customer)
            def computer = Mock(Computer)
            def email = "mail"

            computer.getCustomer() >> customer
            customer.getEmail() >> email

            def computerDamage = new ComputerDamage(id, computer, "temat", "opis", null, ComputerDamageStatus.ASSIGNED_TO_REPAIRER, null, null)

        and:
            computerDamageRepository.findById(id) >> Optional.of(computerDamage)

        when:
            computerDamageService.deleteComputerDamage(id, email)

        then:
            thrown(ComputerDamageIsAcceptedException)
    }

    def "deleteComputerDamage should throw UserNotAllowedException when logged in user is not associated with damage"() {
        given:
            def id = 300
            def customer = Mock(Customer)
            def computer = Mock(Computer)
            def email = "mail"

            computer.getCustomer() >> customer
            customer.getEmail() >> email

            def computerDamage = new ComputerDamage(id, computer, "temat", "opis", null, ComputerDamageStatus.REPORTED, null, null)

        and:
            computerDamageRepository.findById(id) >> Optional.of(computerDamage)

        when:
            computerDamageService.deleteComputerDamage(id, "inny_email")

        then:
            thrown(UserNotAllowedException)
    }

    def 'findUserActiveComputerDamages should return only active users damages'() {
        given:
            def email = "email"
            def customer = Mock(Customer)
            def computer = Mock(Computer)
            def computerList = [computer]
            def damage1 = Mock(ComputerDamage)
            def damage2 = Mock(ComputerDamage)
            def damages = [damage1, damage2]

        and:
            damage1.getStatus() >> ComputerDamageStatus.ASSIGNED_TO_REPAIRER
            damage2.getStatus() >> ComputerDamageStatus.PRICED
            customer.getEmail() >> email
            customer.getComputers() >> computerList
            computer.getDamages() >> damages
            userService.findUserByEmail(*_) >> Optional.of(customer)

        when:
            def result = computerDamageService.findUserActiveComputerDamages(email)

        then:
            result == [damage1]
    }

    def "acceptComputerDamage should throw UserNotAllowedException when logged in user is not associated with damage"() {
        given:
            def id = 300
            def customer = Mock(Customer)
            def computer = Mock(Computer)
            def email = "mail"

            computer.getCustomer() >> customer
            customer.getEmail() >> email

            def computerDamage = new ComputerDamage(id, computer, "temat", "opis", null, ComputerDamageStatus.REPORTED, null, null)

        and:
            computerDamageRepository.findById(id) >> Optional.of(computerDamage)

        when:
            computerDamageService.acceptComputerDamage(id, "inny_email")

        then:
            thrown(UserNotAllowedException)
    }

    def "acceptComputerDamage should throw ComputerDamageNotFoundException when damage with specific id is not found"() {
        given:
            def id = 300
            computerDamageRepository.findById(id) >> Optional.ofNullable(null)

        when:
            computerDamageService.acceptComputerDamage(id, "inny_email")

        then:
            thrown(ComputerDamageNotFoundException)
    }

    def "cancelComputerDamage should throw UserNotAllowedException when logged in user is not associated with damage"() {
        given:
            def id = 300
            def customer = Mock(Customer)
            def computer = Mock(Computer)
            def email = "mail"

            computer.getCustomer() >> customer
            customer.getEmail() >> email

            def computerDamage = new ComputerDamage(id, computer, "temat", "opis", null, ComputerDamageStatus.REPORTED, null, null)

        and:
            computerDamageRepository.findById(id) >> Optional.of(computerDamage)

        when:
            computerDamageService.cancelComputerDamage(id, "inny_email")

        then:
            thrown(UserNotAllowedException)
    }

    def "cancelComputerDamage should throw ComputerDamageNotFoundException when damage with specific id is not found"() {
        given:
            def id = 300
            computerDamageRepository.findById(id) >> Optional.ofNullable(null)

        when:
            computerDamageService.cancelComputerDamage(id, "inny_email")

        then:
            thrown(ComputerDamageNotFoundException)
    }
}
