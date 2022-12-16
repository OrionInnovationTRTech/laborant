package tr.com.orioninc.laborant.security.config;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tr.com.orioninc.laborant.security.repository.UserRepository;
import tr.com.orioninc.laborant.security.model.User;

@RestController
@CrossOrigin
@Log4j2
@AllArgsConstructor
public class FormLogin {
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;


    @PostMapping("/authenticate")
    public ResponseEntity<User> authenticate(@RequestBody String username, String password) {
        // Look up the user in the database using the username
        User user = userRepository.findByUsername(username);

        // If the user exists and the password is correct, return the user
        if (user != null && passwordEncoder.matches(user.getPassword(), password)) {
            log.info("[authenticate] User {} logged in", username);
            return ResponseEntity.ok(user);
        }

        // Otherwise, return an error
        log.warn("[authenticate] User {} failed to log in", username);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}


