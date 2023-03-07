package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.PasswordResetToken;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.PasswordResetTokenRepository;
import tr.com.orioninc.laborant.app.repository.UserRepository;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;
import tr.com.orioninc.laborant.security.config.PasswordConfig;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
@Log4j2
@AllArgsConstructor
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private AsyncService asyncService;

    public PasswordResetService() {
        // Default constructor
    }

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService, @Value("${REACT_APP_API_URL}") String reactAppApiUrl) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public String forgotPasswordByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.info("[forgotPasswordByEmail] User by email {} not found", email);
            throw new NotFoundException("Email not found");
        }

        if (passwordResetTokenRepository.findByUser(user)!=null) {
            passwordResetTokenRepository.deleteByUserId(user.getId());
            log.info("[forgotPasswordByEmail] Old token deleted for user {}", user.getEmail());
        }


        String code = generateCode();
        LocalDateTime codeExpiry = LocalDateTime.now().plusMinutes(15);
        String hashedCode = hash(code);
        if (passwordResetTokenRepository != null) {
            while (passwordResetTokenRepository.findByToken(hashedCode) != null) {
                code = generateCode();
                hashedCode = hash(code);
            }
        }
        asyncService.sendPasswordResetEmail(email, code);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(hashedCode);
        passwordResetToken.setExpiryDate(codeExpiry);
        passwordResetToken.setUser(user);
        passwordResetTokenRepository.save(passwordResetToken);




        log.info("[forgotPasswordByEmail] Code generated for user {}", user.getEmail());


        return "Password reset code sent to your email.";

    }


    public String resetPasswordByEmail(String code, String newPassword) {
        String hashedCode = hash(code);
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(hashedCode);
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
            passwordResetTokenRepository.delete(passwordResetToken);
            log.info("[resetPassword] Code {} is expired for user {}", code, user);
            throw new NotFoundException("Code is expired");
        }

        if (passwordResetToken.getUser().getId() != user.getId()) {
            log.info("[resetPassword] Code {} is not valid for user {}", code, user);
            throw new NotFoundException("Invalid Token");
        }

        user.setPassword(PasswordConfig.passwordEncoder().encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);

        log.info("[resetPassword] Password reset for user {}", user);
        return "Password reset successful.";
    }

    private String generateCode() {
        Random random = new Random();
        int code = random.nextInt(9000000) + 1000000; // Generates a random integer between 1000000 and 9999999
        return String.valueOf(code);
    }

    private String hash(String value) {
        String hashedValue = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            hashedValue = Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedValue;
    }
}
