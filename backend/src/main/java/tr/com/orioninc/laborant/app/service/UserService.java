package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.model.Token;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.app.repository.UserRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;
import tr.com.orioninc.laborant.security.config.PasswordConfig;
import tr.com.orioninc.laborant.security.service.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private LabRepository labRepository;
    private TokenService tokenService;
    private AsyncService asyncService;

    public void addNewUser(User user) {
            if(user.getUsername() != null) {
                if (isUserExistsByUsername(user.getUsername())) {
                    throw new AlreadyExistsException("User with username: " + user.getUsername() + " already exists");
                }
            }
            if (user.getEmail() != null) {
                if (isUserExistsByEmail(user.getEmail())) {
                    throw new AlreadyExistsException("User with email: " + user.getEmail() + " already exists");
                }
            }
                if (user.getUser_role() == null || user.getUser_role().isEmpty()) {
                    user.setUser_role("USER");
                }
                if (user.getPassword() == null || user.getPassword().isEmpty()) {
                    String pass = tokenService.generateCode(6, "alphanumeric");
                    user.setPassword(PasswordConfig.passwordEncoder().encode(pass));
                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        asyncService.sendCredentialsWithEmail(user.getEmail(), user.getUsername(), pass);
                    } else {
                        throw new IllegalArgumentException("You should enter an email for automatic password generation or enter username, password manually");
                    }
                }
                else {
                    if (user.getUsername() == null || user.getUsername().isEmpty()) {
                        throw new IllegalArgumentException("You should enter a username for manual password generation");
                    }
                }
                userRepository.save(user);
                log.info("[addNewUser] User added: {}", user.toString());
    }

    public void addNewUserWithJustEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!isUserExistsByEmail(user.getEmail())) {
            String pass = tokenService.generateCode(6, "alphanumeric");
            user.setPassword(PasswordConfig.passwordEncoder().encode(pass));
            user.setUsername(user.getEmail().split("@")[0]);
            if (user.getUser_role() == null || user.getUser_role().isEmpty()) {
                user.setUser_role("USER");
            }
            userRepository.save(user);
            asyncService.sendCredentialsWithEmail(user.getEmail(), user.getUsername(), pass);
            log.info("[addNewUser] User added: {}", user.getUsername());
        } else {
            log.error("[addNewUser] User {} already exists", user.getUsername());
            throw new AlreadyExistsException("User with email: " + user.getEmail() + " already exists");

        }
    }


    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("[getUserByUsername] User not found");
        }
        return user;
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("[getUserByEmail] User not found");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("[loadUserByUsername] User not found");
        }
        return new CustomUserDetails(user);
    }

    public boolean isUserExistsByUsername(String username) {
        log.info("[isUserExists] Checking if user named '{}' exists", username);
        log.info(Objects.isNull(userRepository.findByUsername(username)) ? "[isUserExists] User not found" : "User found");
        return !Objects.isNull(userRepository.findByUsername(username));
    }

    public boolean isUserExistsByEmail(String email) {
        log.info("[isUserExists] Checking if user with email '{}' exists", email);
        log.info(Objects.isNull(userRepository.findByEmail(email)) ? "[isUserExists] User not found" : "User found");
        return !Objects.isNull(userRepository.findByEmail(email));
    }

    public boolean deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            if (!user.getLabs().isEmpty()) {
                user.setLabs(null);
                userRepository.save(user);
            }
            for (Lab lab : labRepository.findAll()) {
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

    public String getUserRole(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getUser_role();
        } else {
            log.info("[getUserRole] User {} not found", username);
            throw new NotFoundException("User not found");
        }
    }

    public String getUserEmail(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getEmail();
        } else {
            return "email not found";
        }
    }

    public String forgotPasswordByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.info("[forgotPasswordByEmail] User by email {} not found", email);
            throw new NotFoundException("Email not found");
        }

        if (tokenService.findByUser(user) != null) {
            if (tokenService.findByUser(user).getEmail() == null || tokenService.findByUser(user).getEmail() == "") {
                log.info("[forgotPasswordByEmail] Old token deleted for user {}", user.getEmail());
                tokenService.deleteToken(tokenService.findByUser(user));
            }
        }


        String code = tokenService.generateCode(6, "numeric");
        LocalDateTime codeExpiry = LocalDateTime.now().plusMinutes(15);
        String hashedCode = tokenService.hash(code);
        asyncService.sendPasswordResetEmail(email, code);
        Token passwordResetToken = new Token();
        passwordResetToken.setToken(hashedCode);
        passwordResetToken.setExpiryDate(codeExpiry);
        passwordResetToken.setUser(user);
        tokenService.saveToken(passwordResetToken);

        log.info("[forgotPasswordByEmail] Code generated for user {}", user.getEmail());
        return "Password reset code sent to your email.";
    }


    public String resetPasswordByEmail(String code, String newPassword) {
        String hashedCode = tokenService.hash(code);
        Token passwordResetToken = tokenService.findByToken(hashedCode);

        User user = passwordResetToken.getUser();
        if (user == null) {
            log.info("[resetPassword] User not found");
            throw new NotFoundException("User not found");
        }

        if (passwordResetToken == null) {
            log.info("[resetPassword] Code {} is not valid for user {}", code, user);
            throw new NotFoundException("Invalid Token");
        }

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenService.deleteToken(passwordResetToken);
            log.info("[resetPassword] Code {} is expired for user {}", code, user);
            throw new NotFoundException("Code is expired");
        }

        if (passwordResetToken.getUser().getId() != user.getId()) {
            log.info("[resetPassword] Code {} is not valid for user {}", code, user);
            throw new NotFoundException("Invalid Token");
        }

        user.setPassword(PasswordConfig.passwordEncoder().encode(newPassword));
        userRepository.save(user);
        tokenService.deleteToken(passwordResetToken);

        log.info("[resetPassword] Password reset for user {}", user);
        return "Password reset successful.";
    }

    public String sendApprovalEmail(String email, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.info("[sendApprovalEmail] User by username {} not found", username);
            throw new NotFoundException("User not found");
        }
        if (userRepository.findByEmail(email) != null) {
            log.info("[sendApprovalEmail] User by email {} already exists", email);
            throw new AlreadyExistsException("Email already exists");
        }

        if (tokenService.findByUser(user) != null) {
            if (tokenService.findByUser(user).getEmail() != null || tokenService.findByUser(user).getEmail() != "") {
                log.info("[sendApprovalEmail] Old token deleted for user {}", user.getEmail());
                tokenService.deleteToken(tokenService.findByUser(user));
            }
        }
        String code = tokenService.generateCode(10, "numeric");
        LocalDateTime codeExpiry = LocalDateTime.now().plusMinutes(15);
        String hashedCode = tokenService.hash(code);
        asyncService.sendApprovalEmail(email, code);
        Token approvalToken = new Token();
        approvalToken.setEmail(email);
        approvalToken.setToken(hashedCode);
        approvalToken.setExpiryDate(codeExpiry);
        approvalToken.setUser(user);
        tokenService.saveToken(approvalToken);
        log.info("[sendApprovalEmail] Code generated for user {}", user.getEmail());
        return "Approval code sent to your email.";
    }

    public String approveEmail(String code, String username) {
        String hashedCode = tokenService.hash(code);
        Token approvalToken = tokenService.findByToken(hashedCode);

        if (approvalToken == null) {
            log.info("[approveEmail] Code {} is not valid for user {}", code, username);
            throw new NotFoundException("Invalid Token");
        }
        User user = approvalToken.getUser();
        if (user == null) {
            log.info("[approveEmail] User not found");
            throw new NotFoundException("User not found");
        }

        if (approvalToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenService.deleteToken(approvalToken);
            log.info("[approveEmail] Code {} is expired for user {}", code, user.getUsername());
            throw new NotFoundException("Code is expired");
        }

        if (approvalToken.getUser().getId() != user.getId()) {
            log.info("[approveEmail] Code {} is not valid for user {} due to requesting user is not same user with sends the request", code, user.getUsername());
            throw new NotFoundException("Invalid Token");
        }
        if (userRepository.findByEmail(approvalToken.getEmail()) != null) {
            log.info("[approveEmail] User by email {} already exists", approvalToken.getEmail());
            throw new AlreadyExistsException("Email already exists");
        }
        user.setEmail(approvalToken.getEmail());
        userRepository.save(user);
        tokenService.deleteToken(approvalToken);

        log.info("[approveEmail] Email approved for user {}", user);
        return "Email approved.";
    }
}
