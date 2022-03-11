package pl.wolskak.mycomputerservice.service

import org.springframework.boot.test.context.SpringBootTest
import pl.wolskak.mycomputerservice.model.ComputerDamage
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus
import pl.wolskak.mycomputerservice.model.Repairer
import pl.wolskak.mycomputerservice.repository.UserRepository
import pl.wolskak.mycomputerservice.service.exceptions.RepairerHasActiveDamageException
import spock.lang.Specification


@SpringBootTest
class AdminServiceTest extends Specification {

    private UserRepository userRepository = Mock()
    private UserService userService = Mock()
    private AdminService adminService = new AdminService(userRepository, userService)


    def "should delete repairer with specified id"() {
        given:
            def id = 300
            def repairer = new Repairer(id, "Imie", "Nazwisko", "mail", "123456789", "password")
            repairer.setComputerDamages(Collections.emptySet())

        and:
            userRepository.findById(id) >> Optional.of(repairer)

        when:
            adminService.deleteRepairer(id)

        then:
            1 * userRepository.delete(repairer)
    }

    def "should throw exception when repairer has active damage"() {
        given:
            def id = 300
            def repairer = new Repairer(id, "Imie", "Nazwisko", "mail", "123456789", "password")
            ComputerDamage computerDamage = new ComputerDamage()
            computerDamage.setStatus(ComputerDamageStatus.ASSIGNED_TO_REPAIRER)
            Set<ComputerDamage> damages = new HashSet()
            damages.add(computerDamage)
            repairer.setComputerDamages(damages)

        and:
            userRepository.findById(id) >> Optional.of(repairer)

        when:
            adminService.deleteRepairer(id)

        then:
            thrown(RepairerHasActiveDamageException)
    }
}
