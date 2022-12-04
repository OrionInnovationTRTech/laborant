package tr.com.orioninc.laborant.security.authenticate.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.security.authenticate.model.User;
import tr.com.orioninc.laborant.security.authenticate.repository.UserRepository;
import tr.com.orioninc.laborant.security.config.PasswordConfig;

import java.util.List;import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    public void addNewUser(User user) {
        if (!isUserExists(user.getUsername())) {
            user.setPassword(PasswordConfig.passwordEncoder().encode(user.getPassword()));
            userRepository.save(user);
            log.info("[addNewUser] User added: {}", user.toString());
        } else {
            log.info("[addNewUser] User {} already exists", user.getUsername());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("[loadUserByUsername] User not found");
        }
        return new CustomUserDetails(user);
    }

    public boolean isUserExists(String username) {
        log.info("[isUserExists] Checking if user named '{}' exists", username);
        log.info(Objects.isNull(userRepository.findByUsername(username)) ? "[isUserExists] User not found" : "User found");
        return !Objects.isNull(userRepository.findByUsername(username));

    }

    public boolean deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            userRepository.delete(user);
            log.info("[deleteUserByUsername] User {} deleted", username);
            return true;
        } else {
            log.info("[deleteUserByUsername] User {} not found", username);
            return false;
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
