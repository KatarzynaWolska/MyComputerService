package pl.wolskak.mycomputerservice.service

import org.springframework.security.crypto.password.PasswordEncoder
import pl.wolskak.mycomputerservice.controller.forms.EditUserForm
import pl.wolskak.mycomputerservice.model.User
import pl.wolskak.mycomputerservice.repository.UserRepository
import pl.wolskak.mycomputerservice.repository.VerificationTokenRepository
import pl.wolskak.mycomputerservice.service.exceptions.UserAlreadyExistsException
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException
import pl.wolskak.mycomputerservice.service.exceptions.UserNotFoundException
import spock.lang.Specification

class UserServiceTest extends Specification {

    private UserRepository userRepository = Mock()
    private VerificationTokenRepository tokenRepository = Mock()
    private PasswordEncoder passwordEncoder = Mock()

    private UserService userService = new UserService(userRepository, tokenRepository, passwordEncoder)

    def 'findUserById should throw UserNotFoundException when user is not found'() {
        given:
            def id = 300
            userRepository.findById(id) >> Optional.ofNullable(null)

        when:
            userService.findUserById(id)

        then:
            thrown(UserNotFoundException)
    }

    def 'updateUser should throw UserNotAllowedException when user is not found'() {
        given:
            def email = "email"
            userRepository.findByEmail(email) >> Optional.ofNullable(null)

        when:
            userService.updateUser(email, Mock(EditUserForm))

        then:
            thrown(UserNotAllowedException)
    }

    def 'createUserAccount should throw UserAlreadyExistsException when user email already exists in database'() {
        given:
            def email = "email"
            def user = new User("imie", "nazwisko", email, "numer", "haslo", true, "role")

        and:
            userRepository.findByEmail(email) >> Optional.of(user)
            userRepository.findByEmail(email).isPresent() >> true

        when:
            userService.createUserAccount(user)

        then:
            thrown(UserAlreadyExistsException)
    }
}
