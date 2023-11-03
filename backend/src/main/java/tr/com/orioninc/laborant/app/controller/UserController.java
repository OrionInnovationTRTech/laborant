package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.service.UserService;
import tr.com.orioninc.laborant.exception.custom.NotAuthorizedException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Log4j2
public class UserController {

    private UserService userService;

    @PutMapping("/change-password")
    @ApiOperation(value = "Change password")
    public ResponseEntity<Boolean> changePassword(@RequestParam String username, @RequestParam String oldPassword, @RequestParam String newPassword, Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || Objects.equals(auth.getName(), username)) {
            log.info("[changePassword] Changing password for user {}", username);
            return ResponseEntity.ok(userService.changePassword(username, oldPassword, newPassword));
        } else {
            throw new NotAuthorizedException("You are not authorized to change password for this user");
        }
    }

    @PostMapping("/add")
    @ApiOperation(value = "Adding new user to database")
    public ResponseEntity<User> addNewUser(@RequestBody User user, Authentication auth) {
        if (auth == null || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.error("[addNewUser] User {} is not authorized to add new user", auth.getName());
            throw new NotAuthorizedException("You are not authorized to add new user");
        } else {
            userService.addNewUser(user);
            log.info("[addNewUser] User added: {}", user.toString());
            return ResponseEntity.ok(user);
        }

    }

    @PostMapping("/add-user-with-email")
    @ApiOperation(value = "Adding new user to database just with email")
    public ResponseEntity<User> addNewUserWithEmail(@RequestBody User user, Authentication auth) {
        if (auth == null || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.error("[addNewUser] User {} is not authorized to add new user", auth.getName());
            throw new NotAuthorizedException("You are not authorized to add new user");
        } else {
            userService.addNewUserWithJustEmail(user);
            log.info("[addNewUser] User added: {}", user.toString());
            return ResponseEntity.ok(user);
        }

    }

    @PostMapping("/bulk-add")
    @ApiOperation(value = "Bulk adding new users to database just with email")
    public ResponseEntity<String> addBulkUser(@RequestBody List<User> users, Authentication authentication) {
        log.info("[addBulkUser] Called with users: {}", users.toString());
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            int a = 0;
            int b = 0;
            Set<String> failedUsers = new HashSet<>();

            for (User user : users) {
                try {
                    if (user.getUsername() == null || user.getUsername().isEmpty())
                        userService.addNewUserWithJustEmail(user);
                    else
                        userService.addNewUser(user);
                    a++;
                } catch (Exception e) {
                    log.warn("[addBulkUser] User {} could not be added", user.toString());
                    b++;
                    failedUsers.add("Email: " + user.getEmail());
                }
            }
            int c = a + b;
            if (!failedUsers.isEmpty()) {
                StringBuilder failedUserEmails = new StringBuilder();
                for (String failedLab : failedUsers) {
                    failedUserEmails.append(failedLab).append("\n");
                }
                return ResponseEntity.ok("Requested to add with " + c + " users. " + a + " of them were added successfully, " + b + " of them couldn't added due to duplicate credentials. Users couldn't add: \n " + failedUserEmails);
            } else {
                return ResponseEntity.ok("Requested to add with " + c + " users. " + a + " of them were added successfully.");
            }
        } else {
            throw new NotAuthorizedException("You are not authorized to add labs");
        }

    }





    @DeleteMapping("/delete/{username}")
    @ApiOperation(value = "Deleting user from database by giving username as a path variable")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username, Authentication auth) {

        if (auth == null || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.error("[addNewUser] User {} is not authorized to delete a user", auth.getName());
            throw new NotAuthorizedException("You are not authorized to delete user");
        } else {
            if (userService.deleteUserByUsername(username)) {
                log.info("[deleteUser] User {} deleted", username);
                return ResponseEntity.ok("User named " + username + " deleted");
            } else {
                log.info("[deleteUser] User {} not found", username);
                throw new NotFoundException("User named " + username + " not found");
            }
        }
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Getting user by giving username as a path variable")
    public ResponseEntity<User> getUser(@PathVariable("username") String username, Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || auth.getName().equals(username)) {
            if (userService.isUserExistsByUsername(username)) {
                log.info("[getUser] User {} found", username);
                return ResponseEntity.ok(userService.getUserByUsername(username));
            } else {
                log.info("[getUser] User {} not found", username);
                throw new NotFoundException("User named " + username + " not found");
            }
        } else {
            log.error("[addNewUser] User {} is not authorized to get user credentials except itself ", auth.getName());
            throw new NotAuthorizedException("You are not authorized to get any user's info rather than yourself");
        }
    }

    @GetMapping("/{username}/login")
    @ApiOperation(value = "Getting user's login info by giving username as a path variable")
    public ResponseEntity<User> getUserRole(@PathVariable("username") String username, Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || auth.getName().equals(username)) {
            return ResponseEntity.ok(userService.getUserByUsername(username));
        } else {
            log.error("[addNewUser] User {} is not authorized to get user credentials except itself ", auth.getName());
            throw new NotAuthorizedException("You are not authorized to get any user's info rather than yourself");
        }
    }

    @GetMapping("/")
    @ApiOperation(value = "Getting all users as a list of User objects")
    public ResponseEntity<List<User>> getAllUsers(Authentication auth) {
        if (auth == null || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.error("[addNewUser] User {} is not authorized to get all ysers", auth.getName());
            throw new NotAuthorizedException("You are not authorized to get all users data");
        } else {
            if (userService.getAllUsers().isEmpty()) {
                log.info("[getAllUsers] No user found");
                throw new NotFoundException("No user found");
            } else {
                log.info("[getAllUsers] Users found");
                return ResponseEntity.ok(userService.getAllUsers());
            }
        }
    }

    @PutMapping("/reset-password")
    @ApiOperation(value = "Password reset by taking code from email and new password from user")
    public ResponseEntity<String> resetPassword(@RequestParam String code, @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.resetPasswordByEmail(code, newPassword));
    }

    @PutMapping("/forgot-password")
    @ApiOperation(value = "Sending code to users email for resetting password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(userService.forgotPasswordByEmail(email));
    }

    @PutMapping("/send-email-code")
    @ApiOperation(value = "Sending code to users email for add/change email")
    public ResponseEntity<String> sendApprovalEmail(@RequestParam String email, @RequestParam String username, Authentication auth) {
        if (auth.getName().equals(username)) {
            return ResponseEntity.ok(userService.sendApprovalEmail(email, username));
        } else {
            log.info("User is: {} and username is: {}", auth.getName(), username);
            throw new NotAuthorizedException("You are not authorized to change email for this user");
        }
    }

    @PutMapping("/change-email")
    @ApiOperation(value = "Change email by taking code from email from user")
    public ResponseEntity<String> approveEmail(@RequestParam String code, @RequestParam String username, Authentication auth) {
        if (auth.getName().equals(username)) {
            return ResponseEntity.ok(userService.approveEmail(code, username));
        } else {
            log.info("User is: {} and username is: {}", auth.getName(), username);
            throw new NotAuthorizedException("You are not authorized to change email for this user");
        }
    }
}
