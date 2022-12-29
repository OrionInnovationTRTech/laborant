package tr.com.orioninc.laborant.security.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.exception.AlreadyExists;
import tr.com.orioninc.laborant.exception.NotAuthorized;
import tr.com.orioninc.laborant.exception.NotFound;
import tr.com.orioninc.laborant.security.model.User;
import tr.com.orioninc.laborant.security.service.UserService;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Log4j2
public class UserController {

    private UserService userService;

    @PutMapping("/change-password")
    @ApiOperation(value = "Change password")
    public ResponseEntity<Boolean> changePassword(@RequestParam String username, @RequestParam String oldPassword, @RequestParam String newPassword,Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || Objects.equals(auth.getName(), username)) {
            log.info("[changePassword] Changing password for user {}", username);
            return ResponseEntity.ok(userService.changePassword(username, oldPassword, newPassword));
        } else {
            throw new NotAuthorized("You are not authorized to change password for this user");
        }
    }

    @PostMapping("/add")
    @ApiOperation(value = "Adding new user to database")
    public ResponseEntity<User> addNewUser(@RequestBody User user, Authentication auth) {
        if (auth == null || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.error("[addNewUser] User {} is not authorized to add new user", auth.getName());
            throw new NotAuthorized("You are not authorized to add new user");
        }
        else {
            userService.addNewUser(user);
            log.info("[addNewUser] User added: {}", user.toString());
            return ResponseEntity.ok(user);
        }

    }


    @DeleteMapping("/delete/{username}")
    @ApiOperation(value = "Deleting user from database by giving username as a path variable")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username, Authentication auth) {

        if (auth == null || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.error("[addNewUser] User {} is not authorized to delete a user", auth.getName());
            throw new NotAuthorized("You are not authorized to delete user");
        }
        else {
            if (userService.deleteUserByUsername(username)) {
                log.info("[deleteUser] User {} deleted", username);
                return ResponseEntity.ok("User named " + username + " deleted");
            }
            else {
                log.info("[deleteUser] User {} not found", username);
                throw new NotFound("User named " + username + " not found");
            }
        }
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Getting user by giving username as a path variable")
    public ResponseEntity<UserDetails> getUser(@PathVariable("username") String username, Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || auth.getName().equals(username)) {
            if (userService.isUserExists(username)) {
                log.info("[getUser] User {} found", username);
                return ResponseEntity.ok(userService.loadUserByUsername(username));
            } else {
                log.info("[getUser] User {} not found", username);
                throw new NotFound("User named " + username + " not found");
            }
        } else {
            log.error("[addNewUser] User {} is not authorized to get user credentials except itself ", auth.getName());
            throw new NotAuthorized("You are not authorized to get any user's info rather than yourself");
        }
    }

    @GetMapping("/")
    @ApiOperation(value = "Getting all users as a list of User objects")
    public ResponseEntity<List<User>> getAllUsers(Authentication auth) {
        if (auth == null || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.error("[addNewUser] User {} is not authorized to get all ysers", auth.getName());
            throw new NotAuthorized("You are not authorized to get all users data");
        }
        else {
            if (userService.getAllUsers().isEmpty()) {
                log.info("[getAllUsers] No user found");
                throw new NotFound("No user found");
            } else {
                log.info("[getAllUsers] Users found");
                return ResponseEntity.ok(userService.getAllUsers());
            }
        }
    }


}
