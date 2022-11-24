package tr.com.orioninc.laborant.security.authenticate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.security.authenticate.model.User;
import tr.com.orioninc.laborant.security.authenticate.repository.UserRepository;
import tr.com.orioninc.laborant.security.authenticate.service.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Log4j2
public class UserController {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestBody User user){
        userService.addNewUser(user);
        if (user.getId() != null) {
            log.info("User added successfully");
            return ResponseEntity.ok(user);
        } else {
            log.info("User could not be added");
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username){
        if (userService.deleteUserByUsername(username)) {
            log.info("User {} deleted", username);
            return ResponseEntity.ok("User named " + username + " deleted");
        }
        else {
            log.info("User {} not found", username);
            return ResponseEntity.badRequest().body("User " + username + " not found");
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


}
