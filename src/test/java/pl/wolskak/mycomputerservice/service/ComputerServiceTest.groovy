package pl.wolskak.mycomputerservice.service

import org.springframework.boot.test.context.SpringBootTest
import pl.wolskak.mycomputerservice.model.Computer
import pl.wolskak.mycomputerservice.model.ComputerDamage
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus
import pl.wolskak.mycomputerservice.repository.ComputerRepository
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageExistsException
import pl.wolskak.mycomputerservice.service.exceptions.ComputerNotFoundException
import spock.lang.Specification

@SpringBootTest
class ComputerServiceTest extends Specification {

    private ComputerRepository computerRepository = Mock()
    private UserService userService = Mock()
    private ComputerDamageService computerDamageService = Mock()

    private ComputerService computerService = new ComputerService(computerRepository, userService, computerDamageService)

    def 'findComputerById should throw ComputerNotFoundException when computer with id is not found'() {
        given:
            def id = 200
            computerRepository.findById(*_) >> Optional.ofNullable(null)

        when:
            computerService.findComputerById(id)

        then:
            thrown(ComputerNotFoundException)
    }

    def 'deleteComputer should delete computer with specified id'() {
        given:
            def id = 300
            def computer = new Computer(id, "typ", "marka", "model", "rok", "system", "procesor", "karta", "ram", null, null, null)

        and:
            computerRepository.findById(id) >> Optional.of(computer)

        when:
            computerService.deleteComputer(id)

        then:
            1 * computerRepository.delete(computer)
    }

    def 'deleteComputer should throw ComputerDamageExistsException when computer has active damage'() {
        given:
            def id = 200
            def computer = new Computer(id, "typ", "marka", "model", "rok", "system", "procesor", "karta", "ram", null, null, null)

        and:
            computerRepository.findById(id) >> Optional.ofNullable(computer)
            computerDamageService.checkIfComputerHasActiveDamage(id) >> true

        when:
            computerService.deleteComputer(id)

        then:
            thrown(ComputerDamageExistsException)
    }

    def 'findHistoryOfComputerDamages should return only finished computer damages'() {
        given:
            def id = 200
            def computerDamage1 = Mock(ComputerDamage)
            def computerDamage2 = Mock(ComputerDamage)
            def damages = [computerDamage1, computerDamage2]
            def computer = Mock(Computer)

        and:
            computerDamage1.getStatus() >> ComputerDamageStatus.ASSIGNED_TO_REPAIRER
            computerDamage2.getStatus() >> ComputerDamageStatus.FIXED
            computer.getDamages() >> damages
            computerRepository.findById(id) >> Optional.ofNullable(computer)

        when:
            def result = computerService.findHistoryOfComputerDamages(id)

        then:
            result == [computerDamage2]
    }
}
