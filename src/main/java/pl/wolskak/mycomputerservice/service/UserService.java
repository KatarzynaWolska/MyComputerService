package pl.wolskak.mycomputerservice.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wolskak.mycomputerservice.controller.forms.EditUserForm;
import pl.wolskak.mycomputerservice.controller.forms.RegistrationForm;
import pl.wolskak.mycomputerservice.model.Customer;
import pl.wolskak.mycomputerservice.model.User;
import pl.wolskak.mycomputerservice.model.VerificationToken;
import pl.wolskak.mycomputerservice.repository.UserRepository;
import pl.wolskak.mycomputerservice.repository.VerificationTokenRepository;
import pl.wolskak.mycomputerservice.service.exceptions.UserAlreadyExistsException;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotFoundException;
import pl.wolskak.mycomputerservice.utils.UserUtils;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private UserRepository userRepository;
    private VerificationTokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User findCurrentLoggedInUserInfo() throws UserNotFoundException{
        return userRepository.findByEmail(UserUtils.getLoggedInUserName()).orElseThrow(UserNotFoundException::new);
    }

    public void updateUser(String email, EditUserForm userForm) throws UserNotAllowedException {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (!optionalUser.isPresent()) {
            throw new UserNotAllowedException();
        } else {
            User user = optionalUser.get();
            if (!UserUtils.checkLoggedInUserEmail(user.getEmail())) {
                throw new UserNotAllowedException();
            }

            user.updateInfo(
                    userForm.getName(),
                    userForm.getSurname(),
                    userForm.getEmail(),
                    userForm.getPhoneNumber()
            );

            userRepository.save(user);
        }
    }

    public boolean checkLoggedInUserPassword(String password) {
        return userRepository.findByEmail(UserUtils.getLoggedInUserName())
                .map(user -> passwordEncoder.matches(password, user.getPassword())).orElse(false);
    }

    public void updatePassword(String password) throws UserNotFoundException{
        Optional<User> optionalUser = userRepository.findByEmail(UserUtils.getLoggedInUserName());

        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException();
        } else {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
    }

    public User createUserAccount(User user) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(user.getEmail());
        }
        return userRepository.save(user);
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
    }

    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    public VerificationToken getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User createCustomerAccount(RegistrationForm registrationForm) {
        Customer customer = new Customer(
                registrationForm.getName(),
                registrationForm.getSurname(),
                registrationForm.getEmail(),
                registrationForm.getPhoneNumber(),
                passwordEncoder.encode(registrationForm.getPassword()),
                false,
                "ROLE_CUSTOMER"
        );

        return createUserAccount(customer);
    }
}
