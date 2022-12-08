package tr.com.orioninc.laborant.security.authenticate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.security.authenticate.model.User;
import tr.com.orioninc.laborant.security.authenticate.service.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Log4j2
public class UserController {

    private UserService userService;

    @PostMapping("/add")
    @ApiOperation(value = "Adding new user to database")
    public ResponseEntity<String> addNewUser(@RequestBody User user) {
        if (userService.isUserExists(user.getUsername())) {
            log.info("[addNewUser] User {} already exists", user.getUsername());
            return ResponseEntity.badRequest().body("User already exists");
        } else {
            userService.addNewUser(user);
            log.info("[addNewUser] User added: {}", user.toString());
            return ResponseEntity.ok("User Added: \n" + user.toString());
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
            return ResponseEntity.badRequest().body("User " + username + " not found");
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
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/")
    @ApiOperation(value = "Getting all users as a list of User objects")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Getting all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }


}
