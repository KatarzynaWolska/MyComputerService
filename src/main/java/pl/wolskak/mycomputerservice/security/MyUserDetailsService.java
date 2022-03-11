package pl.wolskak.mycomputerservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.wolskak.mycomputerservice.model.User;
import pl.wolskak.mycomputerservice.repository.UserRepository;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //return new MyUserDetails(s);
        Optional<User> userEntity = userRepository.findByEmail(username).map(user -> new User(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPassword(),
                user.isActive(),
                user.getRoles()

        ));
        return userEntity.map(MyUserDetails::new).orElseThrow(
                () -> new UsernameNotFoundException("User not found: " + username)
        );
    }
}
