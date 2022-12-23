package tr.com.orioninc.laborant.security.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.exception.AlreadyExists;
import tr.com.orioninc.laborant.exception.NotFound;
import tr.com.orioninc.laborant.security.model.User;
import tr.com.orioninc.laborant.security.service.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Log4j2
public class UserController {

    private UserService userService;

    @PostMapping("/add")
    @ApiOperation(value = "Adding new user to database")
    public ResponseEntity<User> addNewUser(@RequestBody User user) {
        if (userService.isUserExists(user.getUsername())) {
            log.info("[addNewUser] User {} already exists", user.getUsername());
            throw new AlreadyExists("User " + user.getUsername() + " already exists");
        } else {
            userService.addNewUser(user);
            log.info("[addNewUser] User added: {}", user.toString());
            return ResponseEntity.ok(user);
        }
    }

    @DeleteMapping("/delete/{username}")
    @ApiOperation(value = "Deleting user from database by giving username as a path variable")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        if (userService.deleteUserByUsername(username)) {
            log.info("[deleteUser] User {} deleted", username);
            return ResponseEntity.ok("User named " + username + " deleted");
        } else {
            log.info("[deleteUser] User {} not found", username);
            throw new NotFound("User named " + username + " not found");
        }
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Getting user by giving username as a path variable")
    public ResponseEntity<UserDetails> getUser(@PathVariable("username") String username) {
        if (userService.isUserExists(username)) {
            log.info("[getUser] User {} found", username);
            return ResponseEntity.ok(userService.loadUserByUsername(username));
        } else {
            log.info("[getUser] User {} not found", username);
            throw new NotFound("User named " + username + " not found");
        }
    }

    @GetMapping("/")
    @ApiOperation(value = "Getting all users as a list of User objects")
    public ResponseEntity<List<User>> getAllUsers() {
        if (userService.getAllUsers().isEmpty()) {
            log.info("[getAllUsers] No user found");
            throw new NotFound("No user found");
        } else {
            log.info("[getAllUsers] Users found");
            return ResponseEntity.ok(userService.getAllUsers());
        }
    }


}
