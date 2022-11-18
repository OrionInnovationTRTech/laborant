package tr.com.orioninc.laborant.security.authenticate.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.security.authenticate.model.User;
import tr.com.orioninc.laborant.security.authenticate.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    public void addNewUser(User user) {
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user);
    }

    public boolean deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            userRepository.delete(user);
            log.info("User {} deleted", username);
            return true;
        } else {
            return false;
        }
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
