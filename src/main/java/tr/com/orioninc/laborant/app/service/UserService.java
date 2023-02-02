package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.UserRepository;
import tr.com.orioninc.laborant.security.config.PasswordConfig;
import tr.com.orioninc.laborant.security.service.CustomUserDetails;

import java.util.List;import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private LabRepository labRepository;

    public void addNewUser(User user) {
        if (!isUserExists(user.getUsername())) {
            user.setPassword(PasswordConfig.passwordEncoder().encode(user.getPassword()));
            userRepository.save(user);
            log.info("[addNewUser] User added: {}", user.toString());
        } else {
            log.error("[addNewUser] User {} already exists", user.getUsername());
            throw new AlreadyExistsException("User " + user.getUsername() + " already exists");

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
            if (!user.getLabs().isEmpty()) {
                user.setLabs(null);
                userRepository.save(user);
            }
            for (Lab lab: labRepository.findAll()) {
                if (lab.getReserved() != null && lab.getReservedBy() != null) {
                    if (lab.getReservedBy().equals(user)) {
                        lab.setReservedBy(null);
                        lab.setReserved(false);
                        lab.setReservedUntil(null);
                        labRepository.save(lab);
                    }
                }
            }
            userRepository.delete(user);
            log.info("[deleteUserByUsername] User {} deleted", username);
            return true;
        } else {
            log.info("[deleteUserByUsername] User {} not found", username);
            return false;
        }
    }

    public List<User> getAllUsers() {
        log.info("[getAllUsers] Getting all users");
        return userRepository.findAll();
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            if (PasswordConfig.passwordEncoder().matches(oldPassword, user.getPassword())) {
                user.setPassword(PasswordConfig.passwordEncoder().encode(newPassword));
                userRepository.save(user);
                log.info("[changePassword] Password changed for user {}", username);
                return true;
            } else {
                throw new IllegalArgumentException("Old password is not correct");
            }
        } else {
            log.info("[changePassword] User {} not found", username);
            throw new NotFoundException("User not found");
        }

    }
}
